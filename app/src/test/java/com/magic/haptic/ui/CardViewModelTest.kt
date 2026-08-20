package com.magic.haptic.ui

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.google.common.truth.Truth.assertThat
import com.magic.haptic.card.CardRepository
import com.magic.haptic.card.DeckPresets
import com.magic.haptic.data.AppDataStore
import com.magic.haptic.data.HapticConfig
import com.magic.haptic.data.TriggerResult
import com.magic.haptic.haptic.HapticEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class CardViewModelTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())

    private lateinit var viewModel: CardViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val dataStore =
            PreferenceDataStoreFactory.create(
                scope = testScope,
                produceFile = { File(temporaryFolder.root, "vm.preferences_pb") },
            )
        val appDataStore = AppDataStore(dataStore)
        viewModel =
            CardViewModel(
                dataStore = appDataStore,
                cardRepository = CardRepository(appDataStore, testScope),
                hapticEncoder = HapticEncoder(),
            )
    }

    @After
    fun tearDown() {
        viewModel.close()
        testScope.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun cardAt_returnsDeckCardForValidPosition() {
        assertThat(viewModel.cardAt(1)).isEqualTo(DeckPresets.DEFAULT[0])
        assertThat(viewModel.cardAt(52)).isEqualTo(DeckPresets.DEFAULT[51])
        assertThat(viewModel.cardAt(0)).isNull()
    }

    @Test
    fun patternFor_encodesKnownCard() {
        val pattern = viewModel.patternFor("AS", HapticConfig(100, 300, 150, 500))
        assertThat(pattern).isNotNull()
        assertThat(pattern!!.timings).isNotEmpty()
    }

    @Test
    fun describeTrigger_mapsPositionToCardAndPattern() {
        val display = viewModel.describeTrigger(TriggerResult(1, "card at position one"))
        assertThat(display.card).isEqualTo("AS")
        assertThat(display.patternDescription).isNotEqualTo("--")
        assertThat(display.rawText).isEqualTo("card at position one")
    }
}
