package com.magic.haptic.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppDataStore(private val context: Context) {

    companion object {
        val CURRENT_DECK_ID = stringPreferencesKey("current_deck_id")
        val CUSTOM_DECK_DATA = stringPreferencesKey("custom_deck_data")
        val SPEED_PRESET = stringPreferencesKey("speed_preset")
        val DEBOUNCE_SEC = intPreferencesKey("debounce_sec")
        val NOTIF_TITLE = stringPreferencesKey("notif_title")
        val NOTIF_BODY = stringPreferencesKey("notif_body")
        
        // Custom Haptic values
        val CUSTOM_SHORT = longPreferencesKey("custom_short")
        val CUSTOM_LONG = longPreferencesKey("custom_long")
        val CUSTOM_GAP = longPreferencesKey("custom_gap")
        val CUSTOM_SEP = longPreferencesKey("custom_sep")
    }

    val currentDeckId: Flow<String> = context.dataStore.data.map { it[CURRENT_DECK_ID] ?: "DEFAULT" }
    val customDeckData: Flow<String> = context.dataStore.data.map { it[CUSTOM_DECK_DATA] ?: "" }
    val speedPreset: Flow<String> = context.dataStore.data.map { it[SPEED_PRESET] ?: "NORMAL" }
    
    val customShort: Flow<Long> = context.dataStore.data.map { it[CUSTOM_SHORT] ?: 100L }
    val customLong: Flow<Long> = context.dataStore.data.map { it[CUSTOM_LONG] ?: 300L }
    val customGap: Flow<Long> = context.dataStore.data.map { it[CUSTOM_GAP] ?: 150L }
    val customSep: Flow<Long> = context.dataStore.data.map { it[CUSTOM_SEP] ?: 500L }

    val debounceSec: Flow<Int> = context.dataStore.data.map { it[DEBOUNCE_SEC] ?: 3 }
    val notifTitle: Flow<String> = context.dataStore.data.map { it[NOTIF_TITLE] ?: "System Optimizer" }
    val notifBody: Flow<String> = context.dataStore.data.map { it[NOTIF_BODY] ?: "Running..." }

    val hapticConfig: Flow<HapticConfig> = kotlinx.coroutines.flow.combine(
        customShort, customLong, customGap, customSep, speedPreset
    ) { s, l, g, sep, preset ->
        when (preset) {
            "FAST" -> HapticConfig(80, 200, 100, 400)
            "SLOW" -> HapticConfig(150, 450, 200, 700)
            else -> HapticConfig(s, l, g, sep) // Also for CUSTOM
        }
    }

    suspend fun saveCurrentDeckId(id: String) {
        context.dataStore.edit { it[CURRENT_DECK_ID] = id }
    }

    suspend fun saveCustomDeckData(data: String) {
        context.dataStore.edit { it[CUSTOM_DECK_DATA] = data }
    }

    suspend fun saveSpeedPreset(preset: String) {
        context.dataStore.edit { it[SPEED_PRESET] = preset }
    }

    suspend fun saveDebounceSec(sec: Int) {
        context.dataStore.edit { it[DEBOUNCE_SEC] = sec }
    }

    suspend fun saveCustomDurations(short: Long, long: Long, gap: Long, sep: Long) {
        context.dataStore.edit {
            it[CUSTOM_SHORT] = short
            it[CUSTOM_LONG] = long
            it[CUSTOM_GAP] = gap
            it[CUSTOM_SEP] = sep
        }
    }

    suspend fun saveNotifConfig(title: String, body: String) {
        context.dataStore.edit {
            it[NOTIF_TITLE] = title
            it[NOTIF_BODY] = body
        }
    }
}
