package com.magic.haptic.card

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.google.common.truth.Truth.assertThat
import com.magic.haptic.data.AppDataStore
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
class CardRepositoryTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())

    private lateinit var repository: CardRepository

    @Before
    fun setUp() {
        val dataStore =
            PreferenceDataStoreFactory.create(
                scope = testScope,
                produceFile = { File(temporaryFolder.root, "card-repo.preferences_pb") },
            )
        repository = CardRepository(AppDataStore(dataStore), testScope)
    }

    @After
    fun tearDown() {
        repository.close()
        testScope.cancel()
    }

    @Test
    fun getCard_returnsCorrectCardForValidPositions() {
        assertThat(repository.getCard(1)).isEqualTo("AS")
        assertThat(repository.getCard(13)).isEqualTo("KS")
        assertThat(repository.getCard(14)).isEqualTo("AH")
        assertThat(repository.getCard(52)).isEqualTo("KC")
        assertThat(repository.getCard(1)).isEqualTo(DeckPresets.DEFAULT[0])
        assertThat(repository.getCard(52)).isEqualTo(DeckPresets.DEFAULT[51])
    }

    @Test
    fun getCard_returnsNullForOutOfRangePositions() {
        assertThat(repository.getCard(0)).isNull()
        assertThat(repository.getCard(-1)).isNull()
        assertThat(repository.getCard(53)).isNull()
        assertThat(repository.getCard(100)).isNull()
    }
}
