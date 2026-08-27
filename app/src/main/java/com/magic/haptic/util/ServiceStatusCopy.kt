package com.magic.haptic.util

import com.magic.haptic.data.ServiceStatus

object ServiceStatusCopy {
    fun displayLabel(status: ServiceStatus): String =
        when (status) {
            ServiceStatus.STOPPED -> "STOPPED"
            ServiceStatus.INITIALIZING -> "INITIALIZING"
            ServiceStatus.LISTENING -> "LISTENING"
            ServiceStatus.ERROR -> "ERROR"
        }

    fun clipboardText(status: ServiceStatus): String = "Service status: ${displayLabel(status)}"
}
