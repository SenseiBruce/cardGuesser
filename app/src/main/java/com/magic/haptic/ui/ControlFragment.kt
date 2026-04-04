package com.magic.haptic.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.magic.haptic.R
import com.magic.haptic.data.ServiceEventBus
import com.magic.haptic.data.ServiceStatus
import com.magic.haptic.databinding.FragmentControlBinding
import com.magic.haptic.service.AudioListenerService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ControlFragment : Fragment() {

    private var _binding: FragmentControlBinding? = null
    private val binding get() = _binding!!

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

        binding.btnToggleService.setOnClickListener {
            toggleService()
        }

        observeService()
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
                } else if (status == ServiceStatus.STOPPED) {
                    handler.removeCallbacks(timerRunnable)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            ServiceEventBus.triggerCount.collectLatest { count ->
                binding.tvTriggerCount.text = "Triggers Detected: $count"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            ServiceEventBus.lastTrigger.collectLatest { trigger ->
                if (trigger != null) {
                    binding.tvLastPhrase.text = "Phrase: \"${trigger.rawText}\""
                    binding.tvLastPosition.text = "Position: ${trigger.position}"
                    // Note: Card lookup in UI can be improved by accessing Repo, but for now we show basic info
                    binding.tvLastCard.text = "Card: Detected"
                }
            }
        }
    }

    private fun updateStatusUi(status: ServiceStatus) {
        val (text, color) = when (status) {
            ServiceStatus.STOPPED -> "● STOPPED" to android.R.color.darker_gray
            ServiceStatus.INITIALIZING -> "● INITIALIZING" to android.R.color.holo_orange_light
            ServiceStatus.LISTENING -> "● LISTENING" to android.R.color.holo_green_light
            ServiceStatus.ERROR -> "● ERROR" to android.R.color.holo_red_light
        }
        binding.tvStatus.text = text
        binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), color))
        
        binding.btnToggleService.text = if (status == ServiceStatus.STOPPED) "START LISTENING" else "STOP LISTENING"
    }

    private fun updateDuration() {
        val start = ServiceEventBus.sessionStartTime.value
        if (start == 0L) return
        
        val diff = (System.currentTimeMillis() - start) / 1000
        val hours = diff / 3600
        val minutes = (diff % 3600) / 60
        val seconds = diff % 60
        
        binding.tvDuration.text = String.format("Session Duration: %02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(timerRunnable)
        _binding = null
    }
}
