package com.furianrt.backup.internal.domain

internal interface ServiceLauncher {
    fun launchBackupService()
    fun launchRestoreService()
}