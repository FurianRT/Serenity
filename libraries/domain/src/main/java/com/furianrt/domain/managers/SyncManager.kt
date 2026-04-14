package com.furianrt.domain.managers

import kotlinx.coroutines.flow.Flow

interface SyncManager {
    suspend fun tryStartAutoBackup()
    fun isBackupInProgress(): Boolean
    fun isRestoreInProgress(): Boolean
    fun hasAutoBackupFailure(): Flow<Boolean>
    suspend fun hideAutoBackupFailure()
}