package com.magic.haptic.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.magic.haptic.data.AppDataStore
import com.magic.haptic.databinding.FragmentSettingsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataStore: AppDataStore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataStore = AppDataStore(requireContext())
        setupDeckSpinner()
        setupHapticSpeed()
        setupNotificationDisguise()
        setupDebounce()
    }

    private fun setupDeckSpinner() {
        val presets = listOf("DEFAULT", "MNEMONICA", "ARONSON", "CUSTOM")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, presets)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDeck.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            dataStore.currentDeckId.collectLatest { id ->
                val pos = presets.indexOf(id)
                if (pos >= 0) binding.spinnerDeck.setSelection(pos)
            }
        }

        binding.spinnerDeck.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                lifecycleScope.launch {
                    dataStore.saveCurrentDeckId(presets[position])
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.btnEditCustomDeck.setOnClickListener {
            DeckCustomizerDialog(dataStore).show(childFragmentManager, "DeckCustomizer")
        }
    }

    private fun setupHapticSpeed() {
        viewLifecycleOwner.lifecycleScope.launch {
            dataStore.speedPreset.collectLatest { preset ->
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
            val preset = when (checkedId) {
                binding.rbFast.id -> "FAST"
                binding.rbNormal.id -> "NORMAL"
                binding.rbSlow.id -> "SLOW"
                binding.rbCustom.id -> "CUSTOM"
                else -> "NORMAL"
            }
            lifecycleScope.launch { dataStore.saveSpeedPreset(preset) }
            binding.llCustomHaptic.visibility = if (preset == "CUSTOM") View.VISIBLE else View.GONE
        }
    }

    private fun setupNotificationDisguise() {
        viewLifecycleOwner.lifecycleScope.launch {
            dataStore.notifTitle.collectLatest { title ->
                if (binding.etNotifTitle.text.toString() != title) binding.etNotifTitle.setText(title)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            dataStore.notifBody.collectLatest { body ->
                if (binding.etNotifBody.text.toString() != body) binding.etNotifBody.setText(body)
            }
        }

        binding.etNotifTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                lifecycleScope.launch { dataStore.saveNotifConfig(s.toString(), binding.etNotifBody.text.toString()) }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etNotifBody.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                lifecycleScope.launch { dataStore.saveNotifConfig(binding.etNotifTitle.text.toString(), s.toString()) }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupDebounce() {
        viewLifecycleOwner.lifecycleScope.launch {
            dataStore.debounceSec.collectLatest { sec ->
                if (binding.etDebounce.text.toString() != sec.toString()) binding.etDebounce.setText(sec.toString())
            }
        }

        binding.etDebounce.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val sec = s.toString().toIntOrNull() ?: 3
                lifecycleScope.launch { dataStore.saveDebounceSec(sec) }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        setupCustomHapticValues()
    }

    private fun setupCustomHapticValues() {
        viewLifecycleOwner.lifecycleScope.launch {
            dataStore.customShort.collectLatest { if (binding.etShort.text.isEmpty()) binding.etShort.setText(it.toString()) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            dataStore.customLong.collectLatest { if (binding.etLong.text.isEmpty()) binding.etLong.setText(it.toString()) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            dataStore.customGap.collectLatest { if (binding.etGap.text.isEmpty()) binding.etGap.setText(it.toString()) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            dataStore.customSep.collectLatest { if (binding.etSep.text.isEmpty()) binding.etSep.setText(it.toString()) }
        }

        binding.btnSaveCustomHaptic.setOnClickListener {
            val s = binding.etShort.text.toString().toLongOrNull() ?: 100L
            val l = binding.etLong.text.toString().toLongOrNull() ?: 300L
            val g = binding.etGap.text.toString().toLongOrNull() ?: 150L
            val sep = binding.etSep.text.toString().toLongOrNull() ?: 500L

            // Worst-case duration for King of Clubs (K: L G L G L, Sep, Clubs: L G S)
            // Duration = (4 * L) + (1 * S) + (3 * G) + (1 * SEP)
            val worstCase = (4 * l) + (1 * s) + (3 * g) + (1 * sep)

            if (worstCase > 4000) {
                binding.btnSaveCustomHaptic.error = "Worst-case duration ($worstCase ms) exceeds 4000ms limit!"
            } else {
                binding.btnSaveCustomHaptic.error = null
                lifecycleScope.launch {
                    dataStore.saveCustomDurations(s, l, g, sep)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
