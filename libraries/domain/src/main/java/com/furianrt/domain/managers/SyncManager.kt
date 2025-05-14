package com.furianrt.domain.managers

interface SyncManager {
    suspend fun tryStartAutoBackup()
    fun isBackupInProgress(): Boolean
    fun isRestoreInProgress(): Boolean
}