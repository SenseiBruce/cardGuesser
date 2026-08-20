package com.magic.haptic.data

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ServiceEventBusTest {
    @Before
    fun resetBus() {
        ServiceEventBus.stopSession()
        ServiceEventBus.startSession()
        ServiceEventBus.updateStatus(ServiceStatus.STOPPED)
    }

    @Test
    fun startSession_resetsTriggerState() =
        runTest {
            ServiceEventBus.emitTrigger(TriggerResult(5, "card at position five"))
            ServiceEventBus.startSession()
            assertThat(ServiceEventBus.triggerCount.first()).isEqualTo(0)
            assertThat(ServiceEventBus.lastTrigger.first()).isNull()
            assertThat(ServiceEventBus.sessionStartTime.first()).isGreaterThan(0L)
        }

    @Test
    fun emitTrigger_incrementsCountAndStoresLast() =
        runTest {
            val collected = mutableListOf<TriggerResult>()
            val job =
                launch(UnconfinedTestDispatcher(testScheduler)) {
                    ServiceEventBus.triggerEvent.collect { collected.add(it) }
                }

            ServiceEventBus.emitTrigger(TriggerResult(12, "the number twelve"))
            assertThat(ServiceEventBus.triggerCount.first()).isEqualTo(1)
            assertThat(ServiceEventBus.lastTrigger.first()?.position).isEqualTo(12)
            assertThat(collected).hasSize(1)
            job.cancel()
        }

    @Test
    fun updateStatus_andStopSession() =
        runTest {
            ServiceEventBus.updateStatus(ServiceStatus.LISTENING)
            assertThat(ServiceEventBus.status.first()).isEqualTo(ServiceStatus.LISTENING)
            ServiceEventBus.stopSession()
            assertThat(ServiceEventBus.status.first()).isEqualTo(ServiceStatus.STOPPED)
        }
}
