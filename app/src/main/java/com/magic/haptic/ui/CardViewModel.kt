package com.magic.haptic.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.magic.haptic.card.CardRepository
import com.magic.haptic.data.AppDataStore
import com.magic.haptic.data.HapticConfig
import com.magic.haptic.data.HapticPattern
import com.magic.haptic.data.TriggerResult
import com.magic.haptic.haptic.HapticEncoder
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn

/**
 * Shared UI state for card lookup and haptic pattern encoding.
 */
class CardViewModel(
    private val dataStore: AppDataStore,
    private val cardRepository: CardRepository,
    private val hapticEncoder: HapticEncoder = HapticEncoder(),
) : ViewModel() {
    val currentDeck: StateFlow<List<String>> =
        cardRepository.currentDeck.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            cardRepository.getCurrentDeck(),
        )

    val currentDeckId: StateFlow<String> =
        dataStore.currentDeckId.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            "DEFAULT",
        )

    val hapticConfig: StateFlow<HapticConfig> =
        dataStore.hapticConfig.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            HapticConfig(100, 300, 150, 500),
        )

    fun cardAt(position: Int): String? = cardRepository.getCard(position)

    fun patternFor(
        card: String,
        config: HapticConfig = hapticConfig.value,
    ): HapticPattern? = hapticEncoder.encode(card, config)

    fun describeTrigger(trigger: TriggerResult): TriggerDisplay {
        val card = cardAt(trigger.position)
        val pattern = card?.let { patternFor(it) }
        return TriggerDisplay(
            position = trigger.position,
            rawText = trigger.rawText,
            card = card,
            patternDescription = pattern?.description ?: "--",
        )
    }

    fun close() {
        cardRepository.close()
    }

    override fun onCleared() {
        close()
        super.onCleared()
    }

    data class TriggerDisplay(
        val position: Int,
        val rawText: String,
        val card: String?,
        val patternDescription: String,
    )

    class Factory(
        private val dataStore: AppDataStore,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(CardViewModel::class.java))
            return CardViewModel(dataStore, CardRepository(dataStore)) as T
        }
    }

    companion object {
        suspend fun awaitHapticConfig(dataStore: AppDataStore): HapticConfig = dataStore.hapticConfig.first()
    }
}
