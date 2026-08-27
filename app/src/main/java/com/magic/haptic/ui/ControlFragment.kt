package com.magic.haptic.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.magic.haptic.R
import com.magic.haptic.data.AppDataStore
import com.magic.haptic.data.ServiceEventBus
import com.magic.haptic.data.ServiceStatus
import com.magic.haptic.databinding.FragmentControlBinding
import com.magic.haptic.service.AudioListenerService
import com.magic.haptic.util.SessionSummaryFormatter
import com.magic.haptic.util.LastTriggerFormatter
import com.magic.haptic.util.TriggerCountFormatter
import com.magic.haptic.util.SessionDuration
import com.magic.haptic.util.ServiceStatusCopy
import com.magic.haptic.util.LastCardCopy
import com.magic.haptic.util.LastPhraseCopy
import com.magic.haptic.util.LastPatternCopy
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ControlFragment : Fragment() {
    private var _binding: FragmentControlBinding? = null
    private val binding get() = _binding!!

    private val cardViewModel: CardViewModel by viewModels {
        CardViewModel.Factory(AppDataStore(requireContext()))
    }

    private var glowAnimator: ObjectAnimator? = null
    private var latestDisplay: CardViewModel.TriggerDisplay? = null
    private var latestTriggerCount: Int = 0

    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable =
        object : Runnable {
            override fun run() {
                updateDuration()
                handler.postDelayed(this, 1000)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentControlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnToggleService.setOnClickListener {
            toggleService()
        }
        binding.btnCopySession.setOnClickListener {
            copySessionSummary()
        }

        observeService()
        setupGlowAnimation()
        binding.btnCopyLastTrigger.setOnClickListener { copyLastTrigger() }
        binding.btnCopyTriggerCount.setOnClickListener { copyTriggerCount() }
        binding.tvDuration.setOnClickListener {
            copySessionDuration()
        binding.tvStatus.setOnClickListener {
            copyServiceStatus()
        binding.tvLastCard.setOnClickListener {
            copyLastCard()
        binding.tvLastPhrase.setOnClickListener {
            copyLastPhrase()
        binding.tvLastPattern.setOnClickListener {
            copyLastPattern()
        }
    }

    private fun setupGlowAnimation() {
        glowAnimator =
            ObjectAnimator.ofFloat(binding.vStatusGlow, "alpha", 0f, 0.6f).apply {
                duration = 1500
                repeatMode = ValueAnimator.REVERSE
                repeatCount = ValueAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
            }
    }

    private fun toggleService() {
        val intent = Intent(requireContext(), AudioListenerService::class.java)
        if (ServiceEventBus.status.value == ServiceStatus.STOPPED) {
            ContextCompat.startForegroundService(requireContext(), intent)
        } else {
            requireContext().stopService(intent)
        }
    }

    private fun observeService() {
        viewLifecycleOwner.lifecycleScope.launch {
            ServiceEventBus.status.collectLatest { status ->
                updateStatusUi(status)
                if (status == ServiceStatus.LISTENING) {
                    handler.post(timerRunnable)
                    glowAnimator?.start()
                } else {
                    handler.removeCallbacks(timerRunnable)
                    glowAnimator?.cancel()
                    binding.vStatusGlow.alpha = 0f
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            ServiceEventBus.triggerCount.collectLatest { count ->
                latestTriggerCount = count
                binding.tvTriggerCount.text = count.toString()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            ServiceEventBus.lastTrigger.collectLatest { trigger ->
                if (trigger != null) {
                    val display = cardViewModel.describeTrigger(trigger)
                    latestDisplay = display
                    binding.tvLastPhrase.text = "Phrase: \"${display.rawText}\""
                    binding.tvLastCard.text = display.card ?: "??"
                    binding.tvLastPattern.text =
                        if (display.card != null) {
                            "Pattern: ${display.patternDescription}"
                        } else {
                            "Pattern: -"
                        }
                }
            }
        }
    }

    private fun copySessionSummary() {
        val text =
            SessionSummaryFormatter.format(
                duration = binding.tvDuration.text.toString(),
                triggerCount = binding.tvTriggerCount.text.toString(),
                lastCard = binding.tvLastCard.text.toString(),
                lastPhrase = binding.tvLastPhrase.text.toString(),
            )
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("session", text))
        Toast.makeText(requireContext(), getString(R.string.session_copied), Toast.LENGTH_SHORT).show()
    }

    private fun updateStatusUi(status: ServiceStatus) {
        val (text, colorRes) =
            when (status) {
                ServiceStatus.STOPPED -> "● STOPPED" to R.color.antique_gold
                ServiceStatus.INITIALIZING -> "● INITIALIZING" to R.color.status_yellow
                ServiceStatus.LISTENING -> "● LISTENING" to R.color.status_green
                ServiceStatus.ERROR -> "● ERROR" to R.color.status_red
            }
        binding.tvStatus.text = text
        binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), colorRes))

        binding.tvBtnLabel.text = if (status == ServiceStatus.STOPPED) "START LISTENING" else "STOP LISTENING"
        binding.cardToggleButton.strokeColor = ContextCompat.getColor(requireContext(), colorRes)
    }

    private fun copyServiceStatus() {
        val label = ServiceStatusCopy.clipboardText(ServiceEventBus.status.value)
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("service status", label))
        Toast.makeText(requireContext(), "Copied service status", Toast.LENGTH_SHORT).show()
    private fun copyLastCard() {
        val label = LastCardCopy.clipboardText(binding.tvLastCard.text.toString())
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("last card", label))
        Toast.makeText(requireContext(), "Copied last card", Toast.LENGTH_SHORT).show()
    private fun copyLastPhrase() {
        val label = LastPhraseCopy.clipboardText(binding.tvLastPhrase.text.toString())
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("last phrase", label))
        Toast.makeText(requireContext(), "Copied last phrase", Toast.LENGTH_SHORT).show()
    private fun copyLastPattern() {
        val label = LastPatternCopy.clipboardText(binding.tvLastPattern.text.toString())
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("last pattern", label))
        Toast.makeText(requireContext(), "Copied last pattern", Toast.LENGTH_SHORT).show()
    }

    private fun updateDuration() {
        val start = ServiceEventBus.sessionStartTime.value
        if (start == 0L) return

        val diff = (System.currentTimeMillis() - start) / 1000
        binding.tvDuration.text = SessionDuration.formatElapsedSeconds(diff)
    }

    private fun copySessionDuration() {
        val label = SessionDuration.clipboardText(binding.tvDuration.text.toString())
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("session duration", label))
        Toast.makeText(requireContext(), "Copied session duration", Toast.LENGTH_SHORT).show()
    }

    private fun copyLastTrigger() {
        val display = latestDisplay
        val text =
            if (display == null) {
                "Last detected trigger: none"
            } else {
                LastTriggerFormatter.format(
                    display.position,
                    display.card,
                    display.rawText,
                    display.patternDescription,
                )
            }
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("last-trigger", text))
        Toast.makeText(
            requireContext(),
            getString(R.string.last_trigger_copied),
    private fun copyTriggerCount() {
        val text = TriggerCountFormatter.format(latestTriggerCount)
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("trigger-count", text))
        Toast.makeText(
            requireContext(),
            getString(R.string.trigger_count_copied),
            Toast.LENGTH_SHORT,
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(timerRunnable)
        glowAnimator?.cancel()
        _binding = null
    }
}
