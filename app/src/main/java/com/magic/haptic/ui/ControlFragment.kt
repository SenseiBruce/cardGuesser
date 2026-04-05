package com.magic.haptic.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.magic.haptic.R
import com.magic.haptic.card.CardRepository
import com.magic.haptic.data.AppDataStore
import com.magic.haptic.data.ServiceEventBus
import com.magic.haptic.data.ServiceStatus
import com.magic.haptic.databinding.FragmentControlBinding
import com.magic.haptic.data.HapticConfig
import com.magic.haptic.haptic.HapticEncoder
import com.magic.haptic.service.AudioListenerService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ControlFragment : Fragment() {

    private var _binding: FragmentControlBinding? = null
    private val binding get() = _binding!!

    private var glowAnimator: ObjectAnimator? = null
    private lateinit var cardRepository: CardRepository
    private val hapticEncoder = HapticEncoder()

    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            updateDuration()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentControlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        cardRepository = CardRepository(AppDataStore(requireContext()))

        binding.btnToggleService.setOnClickListener {
            toggleService()
        }

        observeService()
        setupGlowAnimation()
    }

    private fun setupGlowAnimation() {
        glowAnimator = ObjectAnimator.ofFloat(binding.vStatusGlow, "alpha", 0f, 0.6f).apply {
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
                binding.tvTriggerCount.text = count.toString()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            ServiceEventBus.lastTrigger.collectLatest { trigger ->
                if (trigger != null) {
                    binding.tvLastPhrase.text = "Phrase: \"${trigger.rawText}\""
                    
                    // Lookup full card identity
                    val deck = cardRepository.currentDeck.first()
                    val card = if (trigger.position in 1..52) deck[trigger.position - 1] else null
                    
                    if (card != null) {
                        binding.tvLastCard.text = card
                        val config = AppDataStore(requireContext()).hapticConfig.first()
                        val pattern = hapticEncoder.encode(card, config)
                        binding.tvLastPattern.text = "Pattern: ${pattern?.description ?: "--"}"
                    } else {
                        binding.tvLastCard.text = "??"
                        binding.tvLastPattern.text = "Pattern: -"
                    }
                }
            }
        }
    }

    private fun updateStatusUi(status: ServiceStatus) {
        val (text, colorRes) = when (status) {
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

    private fun updateDuration() {
        val start = ServiceEventBus.sessionStartTime.value
        if (start == 0L) return
        
        val diff = (System.currentTimeMillis() - start) / 1000
        val hours = diff / 3600
        val minutes = (diff % 3600) / 60
        val seconds = diff % 60
        
        binding.tvDuration.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(timerRunnable)
        glowAnimator?.cancel()
        _binding = null
    }
}
