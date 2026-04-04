package com.magic.haptic.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.magic.haptic.card.DeckValidator
import com.magic.haptic.data.AppDataStore
import com.magic.haptic.databinding.DialogCustomDeckBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DeckCustomizerDialog(private val appDataStore: AppDataStore) : DialogFragment() {

    private var _binding: DialogCustomDeckBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogCustomDeckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val currentData = appDataStore.customDeckData.first()
            binding.etCustomDeck.setText(currentData)
        }

        binding.btnValidate.setOnClickListener {
            val result = DeckValidator.validate(binding.etCustomDeck.text.toString())
            binding.tvValidationStatus.text = "Status: ${result.message}"
        }

        binding.btnSaveDeck.setOnClickListener {
            val result = DeckValidator.validate(binding.etCustomDeck.text.toString())
            if (result.isValid) {
                lifecycleScope.launch {
                    appDataStore.saveCustomDeckData(binding.etCustomDeck.text.toString())
                    dismiss()
                }
            } else {
                binding.tvValidationStatus.text = "Status: ${result.message}"
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
