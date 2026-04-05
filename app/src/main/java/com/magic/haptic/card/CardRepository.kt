package com.magic.haptic.card

import com.magic.haptic.data.AppDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CardRepository(private val dataStore: AppDataStore) {

    private val _currentDeck = kotlinx.coroutines.flow.MutableStateFlow(DeckPresets.DEFAULT)
    val currentDeck: kotlinx.coroutines.flow.StateFlow<List<String>> = _currentDeck

    private var customDeck: List<String> = emptyList()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.customDeckData.collectLatest { data ->
                customDeck = parseCustomDeck(data)
            }
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.currentDeckId.collectLatest { id ->
                _currentDeck.value = when (id) {
                    "MNEMONICA" -> DeckPresets.MNEMONICA
                    "ARONSON" -> DeckPresets.ARONSON
                    "CUSTOM" -> if (customDeck.size == 52) customDeck else DeckPresets.DEFAULT
                    else -> DeckPresets.DEFAULT
                }
            }
        }
    }

    fun getCard(position: Int): String? {
        val deck = _currentDeck.value
        if (position < 1 || position > deck.size) return null
        return deck[position - 1]
    }

    fun getCurrentDeck(): List<String> = _currentDeck.value

    private fun parseCustomDeck(data: String): List<String> {
        return data.split(",")
            .map { it.trim().uppercase() }
            .filter { it.isNotEmpty() }
    }
}
