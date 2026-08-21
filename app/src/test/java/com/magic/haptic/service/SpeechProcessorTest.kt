package com.magic.haptic.service

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.google.common.truth.Truth.assertThat
import com.magic.haptic.card.CardRepository
import com.magic.haptic.card.DeckPresets
import com.magic.haptic.data.AppDataStore
import com.magic.haptic.data.HapticConfig
import com.magic.haptic.haptic.HapticEncoder
import com.magic.haptic.parser.NumberWordConverter
import com.magic.haptic.parser.TriggerParser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class SpeechProcessorTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())

    private lateinit var processor: SpeechProcessor
    private lateinit var repository: CardRepository
    private val config = HapticConfig(100, 300, 150, 500)

    @Before
    fun setUp() {
        val dataStore =
            PreferenceDataStoreFactory.create(
                scope = testScope,
                produceFile = { File(temporaryFolder.root, "speech.preferences_pb") },
            )
        repository = CardRepository(AppDataStore(dataStore), testScope)
        val parser = TriggerParser(NumberWordConverter()).also { it.setDebounce(0) }
        processor = SpeechProcessor(parser, repository, HapticEncoder())
    }

    @After
    fun tearDown() {
        repository.close()
        testScope.cancel()
    }

    @Test
    fun process_mapsSpokenPositionToCardAndPattern() {
        val result = processor.process("card at position one", config)
        assertThat(result).isNotNull()
        assertThat(result!!.trigger.position).isEqualTo(1)
        assertThat(result.card).isEqualTo(DeckPresets.DEFAULT[0])
        assertThat(result.pattern.timings).isNotEmpty()
        assertThat(result.logEntry.isMatch).isTrue()
    }

    @Test
    fun process_returnsNullWhenNoTrigger() {
        assertThat(processor.process("hello there", config)).isNull()
    }

    @Test
    fun extractAndProcess_readsVoskJsonThenMaps() {
        val json = """{"text":"the number twelve"}"""
        val result = processor.extractAndProcess(json, "text", config)
        assertThat(result).isNotNull()
        assertThat(result!!.trigger.position).isEqualTo(12)
        assertThat(result.card).isEqualTo(DeckPresets.DEFAULT[11])
    }
}
