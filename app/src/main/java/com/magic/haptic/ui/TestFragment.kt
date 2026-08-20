package com.magic.haptic.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.magic.haptic.card.CardRepository
import com.magic.haptic.data.AppDataStore
import com.magic.haptic.data.HapticConfig
import com.magic.haptic.data.ServiceEventBus
import com.magic.haptic.databinding.FragmentTestBinding
import com.magic.haptic.haptic.HapticEncoder
import com.magic.haptic.haptic.HapticPlayer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TestFragment : Fragment() {
    private var _binding: FragmentTestBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SpeechLogAdapter
    private lateinit var cardRepository: CardRepository
    private lateinit var encoder: HapticEncoder
    private lateinit var player: HapticPlayer
    private lateinit var appDataStore: AppDataStore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        appDataStore = AppDataStore(requireContext())
        cardRepository = CardRepository(appDataStore)
        encoder = HapticEncoder()
        player = HapticPlayer(requireContext())

        setupSpeechLog()
        setupManualVibrate()
        setupQuickTest()
        observeSpeech()
    }

    private fun setupSpeechLog() {
        adapter = SpeechLogAdapter()
        binding.rvSpeechLog.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSpeechLog.adapter = adapter
    }

    private fun setupManualVibrate() {
        binding.btnVibrateManual.setOnClickListener {
            val posStr = binding.etManualPosition.text.toString()
            val position = posStr.toIntOrNull()
            if (position != null && position in 1..52) {
                testVibrate(position)
            } else {
                Toast.makeText(requireContext(), "Enter a valid position (1-52)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun testVibrate(position: Int) {
        val card = cardRepository.getCard(position)
        if (card != null) {
            binding.tvManualCardInfo.text = "Card: $card"
            val config = HapticConfig(100, 300, 150, 500) // Default for test
            val pattern = encoder.encode(card, config)
            if (pattern != null) {
                player.vibrate(pattern)
            }
        }
    }

    private fun setupQuickTest() {
        val quickTestCards =
            listOf(
                "AS",
                "KC",
                "6S",
                "QH",
                "10D",
                "JC",
                "4H",
                "9S",
            )

        binding.cardGrid.removeAllViews()
        quickTestCards.forEach { cardCode ->
            val button =
                Button(requireContext()).apply {
                    text = cardCode
                    setOnClickListener {
                        val config = HapticConfig(100, 300, 150, 500)
                        val pattern = this@TestFragment.encoder.encode(cardCode, config)
                        if (pattern != null) this@TestFragment.player.vibrate(pattern)
                    }
                }
            binding.cardGrid.addView(button)
        }
    }

    private fun observeSpeech() {
        viewLifecycleOwner.lifecycleScope.launch {
            ServiceEventBus.speechLog.collectLatest { entry ->
                adapter.addEntry(entry)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
