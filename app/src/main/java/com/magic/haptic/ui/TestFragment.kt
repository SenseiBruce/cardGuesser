package com.magic.haptic.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.magic.haptic.R
import com.magic.haptic.card.CardRepository
import com.magic.haptic.data.AppDataStore
import com.magic.haptic.data.HapticConfig
import com.magic.haptic.data.ServiceEventBus
import com.magic.haptic.databinding.FragmentTestBinding
import com.magic.haptic.haptic.DrillRound
import com.magic.haptic.haptic.HapticDrill
import com.magic.haptic.haptic.HapticEncoder
import com.magic.haptic.haptic.HapticPlayer
import com.magic.haptic.util.SpeechLogFormatter
import com.magic.haptic.util.ManualCardInfoCopy
import com.magic.haptic.util.DrillStatsCopy
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
    private val drill = HapticDrill()
    private var currentRound: DrillRound? = null

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
        binding.btnClearSpeechLog.setOnClickListener {
            adapter.clear()
        }
        setupManualVibrate()
        setupQuickTest()
        setupDrill()
        observeSpeech()
        binding.btnCopySpeechLog.setOnClickListener { copySpeechLog() }

        binding.tvManualCardInfo.setOnClickListener {
            copyManualCardInfo()
        }
        binding.tvDrillStatus.setOnLongClickListener {
            copyDrillStats()
            true
        }
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

    private fun setupDrill() {
        val optionButtons =
            listOf(binding.btnDrill1, binding.btnDrill2, binding.btnDrill3, binding.btnDrill4)
        binding.btnStartDrill.setOnClickListener { startDrillRound() }
        optionButtons.forEach { button ->
            button.setOnClickListener {
                val guess = button.text.toString()
                gradeDrill(guess)
            }
        }
    }

    private fun startDrillRound() {
        val round = drill.nextRound(cardRepository.getCurrentDeck())
        currentRound = round
        val config = HapticConfig(100, 300, 150, 500)
        val pattern = encoder.encode(round.target, config)
        if (pattern != null) {
            player.vibrate(pattern)
        }
        val buttons = listOf(binding.btnDrill1, binding.btnDrill2, binding.btnDrill3, binding.btnDrill4)
        buttons.forEachIndexed { index, button ->
            button.text = round.options[index]
            button.isEnabled = true
        }
        binding.tvDrillStatus.text =
            "Pattern playing. Choose the card. Score ${drill.stats.correct}/${drill.stats.attempts} streak ${drill.stats.streak}"
        binding.btnStartDrill.text = "REPLAY"
    }

    private fun gradeDrill(guess: String) {
        val round = currentRound ?: return
        val correct = drill.recordGuess(round, guess)
        val score = "Score ${drill.stats.correct}/${drill.stats.attempts} streak ${drill.stats.streak} (best ${drill.stats.bestStreak})"
        binding.tvDrillStatus.text =
            if (correct) {
                "Correct: ${round.target}. $score"
            } else {
                "Miss. It was ${round.target}. $score"
            }
        listOf(binding.btnDrill1, binding.btnDrill2, binding.btnDrill3, binding.btnDrill4)
            .forEach { it.isEnabled = false }
        binding.btnStartDrill.text = "NEXT"
        currentRound = null
    }

    private fun observeSpeech() {
        viewLifecycleOwner.lifecycleScope.launch {
            ServiceEventBus.speechLog.collectLatest { entry ->
                adapter.addEntry(entry)
            }
        }
    }

    private fun copySpeechLog() {
        val text = SpeechLogFormatter.format(adapter.snapshot())
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("perception-log", text))
        Toast.makeText(requireContext(), getString(R.string.speech_log_copied), Toast.LENGTH_SHORT)
            .show()
    }

    private fun copyDrillStats() {
        val stats = drill.stats
        val label =
            DrillStatsCopy.clipboardText(stats.correct, stats.attempts, stats.streak, stats.bestStreak)
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("drill-stats", label))
        Toast.makeText(requireContext(), "Copied drill stats", Toast.LENGTH_SHORT).show()
    }

    private fun copyManualCardInfo() {
        val label = ManualCardInfoCopy.clipboardText(binding.tvManualCardInfo.text)
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("manual lookup", label))
        Toast.makeText(requireContext(), "Copied manual lookup", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
