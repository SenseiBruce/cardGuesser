package com.magic.haptic.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class AppDataStoreTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())

    private fun createStore(name: String): AppDataStore {
        val dataStore =
            PreferenceDataStoreFactory.create(
                scope = testScope,
                produceFile = { File(temporaryFolder.root, "$name.preferences_pb") },
            )
        return AppDataStore(dataStore)
    }

    @Test
    fun saveCurrentDeckId_persistsAndRoundTrips() =
        testScope.runTest {
            val store = createStore("deck")
            store.saveCurrentDeckId("MNEMONICA")
            assertThat(store.currentDeckId.first()).isEqualTo("MNEMONICA")
        }

    @Test
    fun saveSpeedPreset_persistsAndRoundTrips() =
        testScope.runTest {
            val store = createStore("speed")
            store.saveSpeedPreset("FAST")
            assertThat(store.speedPreset.first()).isEqualTo("FAST")
        }

    @Test
    fun saveNotifConfig_persistsAndRoundTrips() =
        testScope.runTest {
            val store = createStore("notif")
            store.saveNotifConfig("Battery Saver", "Optimizing in background")
            assertThat(store.notifTitle.first()).isEqualTo("Battery Saver")
            assertThat(store.notifBody.first()).isEqualTo("Optimizing in background")
        }

    @Test
    fun saveManualPosition_persistsAndRoundTrips() =
        testScope.runTest {
            val store = createStore("manual")
            store.saveManualPosition("17")
            assertThat(store.manualPosition.first()).isEqualTo("17")
        }

    @Test
    fun saveDrillStats_persistsAndRoundTrips() =
        testScope.runTest {
            val store = createStore("drill")
            store.saveDrillStats(
                com.magic.haptic.haptic.DrillStats(
                    correct = 4,
                    attempts = 6,
                    streak = 2,
                    bestStreak = 3,
                ),
            )
            val stats = store.drillStats.first()
            assertThat(stats.correct).isEqualTo(4)
            assertThat(stats.attempts).isEqualTo(6)
            assertThat(stats.streak).isEqualTo(2)
            assertThat(stats.bestStreak).isEqualTo(3)
        }

    @Test
    fun defaults_areUsedWhenUnset() =
        testScope.runTest {
            val store = createStore("defaults")
            assertThat(store.currentDeckId.first()).isEqualTo("DEFAULT")
            assertThat(store.speedPreset.first()).isEqualTo("NORMAL")
            assertThat(store.notifTitle.first()).isEqualTo("System Optimizer")
            assertThat(store.notifBody.first()).isEqualTo("Running...")
        }
}
