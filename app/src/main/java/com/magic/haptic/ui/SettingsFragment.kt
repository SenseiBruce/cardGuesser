package com.magic.haptic.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.magic.haptic.R
import com.magic.haptic.data.AppDataStore
import com.magic.haptic.data.HapticConfig
import com.magic.haptic.databinding.FragmentSettingsBinding
import com.magic.haptic.util.SettingsSummaryFormatter
import com.magic.haptic.util.HapticConfigFormatter
import com.magic.haptic.util.CurrentDeckCopy
import com.magic.haptic.data.AppDataStore
import com.magic.haptic.databinding.FragmentSettingsBinding
import com.magic.haptic.util.HapticSpeedCopy
import com.magic.haptic.data.AppDataStore
import com.magic.haptic.databinding.FragmentSettingsBinding
import com.magic.haptic.util.NotifTitleCopy
import com.magic.haptic.util.NotifBodyCopy
import com.magic.haptic.util.DebounceCopy
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var appDataStore: AppDataStore
    private var latestPreset: String = "NORMAL"
    private var latestConfig: HapticConfig = HapticConfig(100, 300, 150, 500)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        appDataStore = AppDataStore(requireContext())
        setupDeckSpinner()
        setupHapticSpeed()
        setupNotificationDisguise()
        setupDebounce()
        binding.btnCopySettings.setOnClickListener { copySettingsSummary() }
        viewLifecycleOwner.lifecycleScope.launch {
            appDataStore.hapticConfig.collectLatest { config ->
                latestConfig = config
            }
        }
        binding.btnCopyHapticConfig.setOnClickListener { copyHapticConfig() }

        binding.tvStackOrder.setOnClickListener {
            copyCurrentDeck()

        binding.tvHapticSensitivity.setOnClickListener {
            copyHapticSpeed()

        binding.tvStealthDisguise.setOnClickListener {
            copyNotifTitle()

        binding.tilNotifBody.setOnClickListener {
            copyNotifBody()
        }
        binding.etNotifBody.setOnLongClickListener {
            copyNotifBody()

        binding.tilDebounce.setOnClickListener {
            copyDebounce()
        }
        binding.etDebounce.setOnLongClickListener {
            copyDebounce()
            true
        }
    }

    private fun setupDeckSpinner() {
        val presets = listOf("DEFAULT", "MNEMONICA", "ARONSON", "CUSTOM")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, presets)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDeck.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            appDataStore.currentDeckId.collectLatest { id ->
                val pos = presets.indexOf(id)
                if (pos >= 0) binding.spinnerDeck.setSelection(pos)
            }
        }

        binding.spinnerDeck.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    lifecycleScope.launch {
                        appDataStore.saveCurrentDeckId(presets[position])
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.btnEditCustomDeck.setOnClickListener {
            DeckCustomizerDialog(appDataStore).show(childFragmentManager, "DeckCustomizer")
        }
    }

    private fun setupHapticSpeed() {
        viewLifecycleOwner.lifecycleScope.launch {
            appDataStore.speedPreset.collectLatest { preset ->
                latestPreset = preset
                when (preset) {
                    "FAST" -> binding.rbFast.isChecked = true
                    "NORMAL" -> binding.rbNormal.isChecked = true
                    "SLOW" -> binding.rbSlow.isChecked = true
                    "CUSTOM" -> {
                        binding.rbCustom.isChecked = true
                        binding.llCustomHaptic.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.rgHapticSpeed.setOnCheckedChangeListener { _, checkedId ->
            val preset =
                when (checkedId) {
                    binding.rbFast.id -> "FAST"
                    binding.rbNormal.id -> "NORMAL"
                    binding.rbSlow.id -> "SLOW"
                    binding.rbCustom.id -> "CUSTOM"
                    else -> "NORMAL"
                }
            lifecycleScope.launch { appDataStore.saveSpeedPreset(preset) }
            binding.llCustomHaptic.visibility = if (preset == "CUSTOM") View.VISIBLE else View.GONE
        }
    }

    private fun setupNotificationDisguise() {
        viewLifecycleOwner.lifecycleScope.launch {
            appDataStore.notifTitle.collectLatest { title ->
                if (binding.etNotifTitle.text.toString() != title) binding.etNotifTitle.setText(title)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            appDataStore.notifBody.collectLatest { body ->
                if (binding.etNotifBody.text.toString() != body) binding.etNotifBody.setText(body)
            }
        }

        binding.etNotifTitle.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    lifecycleScope.launch { appDataStore.saveNotifConfig(s.toString(), binding.etNotifBody.text.toString()) }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {}
            },
        )

        binding.etNotifBody.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    lifecycleScope.launch { appDataStore.saveNotifConfig(binding.etNotifTitle.text.toString(), s.toString()) }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {}
            },
        )
    }

    private fun setupDebounce() {
        viewLifecycleOwner.lifecycleScope.launch {
            appDataStore.debounceSec.collectLatest { sec ->
                if (binding.etDebounce.text.toString() != sec.toString()) binding.etDebounce.setText(sec.toString())
            }
        }

        binding.etDebounce.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val sec = s.toString().toIntOrNull() ?: 3
                    lifecycleScope.launch { appDataStore.saveDebounceSec(sec) }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {}
            },
        )

        setupCustomHapticValues()
    }

    private fun setupCustomHapticValues() {
        viewLifecycleOwner.lifecycleScope.launch {
            appDataStore.customShort.collectLatest { if (binding.etShort.text?.isEmpty() == true) binding.etShort.setText(it.toString()) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            appDataStore.customLong.collectLatest { if (binding.etLong.text?.isEmpty() == true) binding.etLong.setText(it.toString()) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            appDataStore.customGap.collectLatest { if (binding.etGap.text?.isEmpty() == true) binding.etGap.setText(it.toString()) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            appDataStore.customSep.collectLatest { if (binding.etSep.text?.isEmpty() == true) binding.etSep.setText(it.toString()) }
        }

        binding.btnSaveCustomHaptic.setOnClickListener {
            val s = binding.etShort.text.toString().toLongOrNull() ?: 100L
            val l = binding.etLong.text.toString().toLongOrNull() ?: 300L
            val g = binding.etGap.text.toString().toLongOrNull() ?: 150L
            val sep = binding.etSep.text.toString().toLongOrNull() ?: 500L

            val worstCase = (4 * l) + (1 * s) + (3 * g) + (1 * sep)

            if (worstCase > 4000) {
                binding.btnSaveCustomHaptic.error = "Worst-case duration ($worstCase ms) exceeds 4000ms limit!"
            } else {
                binding.btnSaveCustomHaptic.error = null
                lifecycleScope.launch {
                    appDataStore.saveCustomDurations(s, l, g, sep)
                }
            }
        }
    }

    private fun copySettingsSummary() {
        val speed =
            when {
                binding.rbFast.isChecked -> "FAST"
                binding.rbSlow.isChecked -> "SLOW"
                binding.rbCustom.isChecked -> "CUSTOM"
                else -> "NORMAL"
            }
        val deck = binding.spinnerDeck.selectedItem?.toString() ?: "DEFAULT"
        val text =
            SettingsSummaryFormatter.format(
                deckId = deck,
                speedPreset = speed,
                debounceSec = binding.etDebounce.text?.toString().orEmpty().ifBlank { "3" },
                notifTitle = binding.etNotifTitle.text?.toString().orEmpty(),
                notifBody = binding.etNotifBody.text?.toString().orEmpty(),
            )
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("settings", text))
        Toast.makeText(requireContext(), getString(R.string.settings_copied), Toast.LENGTH_SHORT)
            .show()
    private fun copyHapticConfig() {
        val text = HapticConfigFormatter.format(latestPreset, latestConfig)
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("haptic-config", text))
        Toast.makeText(
            requireContext(),
            getString(R.string.haptic_config_copied),
            Toast.LENGTH_SHORT,
        ).show()
    private fun copyCurrentDeck() {
        val selected = binding.spinnerDeck.selectedItem?.toString()
        val label = CurrentDeckCopy.clipboardText(selected)
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("current deck", label))
        Toast.makeText(requireContext(), "Copied current deck", Toast.LENGTH_SHORT).show()
    private fun copyHapticSpeed() {
        val preset =
            when {
                binding.rbFast.isChecked -> "FAST"
                binding.rbNormal.isChecked -> "NORMAL"
                binding.rbSlow.isChecked -> "SLOW"
                binding.rbCustom.isChecked -> "CUSTOM"
                else -> ""
            }
        val label = HapticSpeedCopy.clipboardText(preset)
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("haptic speed", label))
        Toast.makeText(requireContext(), "Copied haptic speed", Toast.LENGTH_SHORT).show()
    private fun copyNotifTitle() {
        val label = NotifTitleCopy.clipboardText(binding.etNotifTitle.text)
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("notification title", label))
        Toast.makeText(requireContext(), "Copied notification title", Toast.LENGTH_SHORT).show()
    private fun copyNotifBody() {
        val label = NotifBodyCopy.clipboardText(binding.etNotifBody.text)
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("notification body", label))
        Toast.makeText(requireContext(), "Copied notification body", Toast.LENGTH_SHORT).show()
    private fun copyDebounce() {
        val label = DebounceCopy.clipboardText(binding.etDebounce.text)
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("debounce", label))
        Toast.makeText(requireContext(), "Copied debounce", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
