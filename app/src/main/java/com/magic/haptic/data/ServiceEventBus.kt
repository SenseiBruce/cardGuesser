package com.magic.haptic.data

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

object ServiceEventBus {
    private val _status = MutableStateFlow(ServiceStatus.STOPPED)
    val status = _status.asStateFlow()

    private val _speechLog = MutableSharedFlow<SpeechLogEntry>(replay = 0)
    val speechLog = _speechLog.asSharedFlow()

    private val _triggerEvent = MutableSharedFlow<TriggerResult>(replay = 0)
    val triggerEvent = _triggerEvent.asSharedFlow()

    private val _lastTrigger = MutableStateFlow<TriggerResult?>(null)
    val lastTrigger = _lastTrigger.asStateFlow()

    private val _triggerCount = MutableStateFlow(0)
    val triggerCount = _triggerCount.asStateFlow()

    private val _sessionStartTime = MutableStateFlow(0L)
    val sessionStartTime = _sessionStartTime.asStateFlow()

    fun updateStatus(newStatus: ServiceStatus) {
        _status.value = newStatus
    }

    suspend fun emitSpeechLog(entry: SpeechLogEntry) {
        _speechLog.emit(entry)
    }

    suspend fun emitTrigger(result: TriggerResult) {
        _triggerEvent.emit(result)
        _lastTrigger.value = result
        _triggerCount.value += 1
    }

    fun startSession() {
        _sessionStartTime.value = System.currentTimeMillis()
        _triggerCount.value = 0
        _lastTrigger.value = null
    }

    fun stopSession() {
        _status.value = ServiceStatus.STOPPED
    }
}
