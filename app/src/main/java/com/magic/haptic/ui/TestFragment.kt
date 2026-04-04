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
import com.magic.haptic.data.*
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
    private lateinit var hapticEncoder: HapticEncoder
    private lateinit var hapticPlayer: HapticPlayer
    private lateinit var dataStore: AppDataStore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataStore = AppDataStore(requireContext())
        cardRepository = CardRepository(dataStore)
        hapticEncoder = HapticEncoder()
        hapticPlayer = HapticPlayer(requireContext())

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
            val pattern = hapticEncoder.encode(card, config)
            if (pattern != null) {
                hapticPlayer.vibrate(pattern)
            }
        }
    }

    private fun setupQuickTest() {
        val quickTestCards = listOf(
            "AS", "KC", "6S", "QH", "10D", "JC", "4H", "9S"
        )
        
        binding.cardGrid.removeAllViews()
        quickTestCards.forEach { card ->
            val button = Button(requireContext()).apply {
                text = card
                setOnClickListener { 
                    val config = HapticConfig(100, 300, 150, 500)
                    val pattern = hapticEncoder.encode(card, config)
                    if (pattern != null) hapticPlayer.vibrate(pattern)
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
