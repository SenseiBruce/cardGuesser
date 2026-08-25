package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import com.magic.haptic.data.ServiceStatus
import org.junit.Test

class ServiceStatusCopyTest {
    @Test
    fun clipboardText_includesStatusName() {
        assertThat(ServiceStatusCopy.displayLabel(ServiceStatus.LISTENING)).isEqualTo("LISTENING")
        assertThat(ServiceStatusCopy.clipboardText(ServiceStatus.STOPPED))
            .isEqualTo("Service status: STOPPED")
        assertThat(ServiceStatusCopy.clipboardText(ServiceStatus.ERROR))
            .isEqualTo("Service status: ERROR")
        assertThat(ServiceStatusCopy.clipboardText(ServiceStatus.INITIALIZING))
            .isEqualTo("Service status: INITIALIZING")
    }
}
