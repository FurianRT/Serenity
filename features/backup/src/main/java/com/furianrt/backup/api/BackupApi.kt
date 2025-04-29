package com.furianrt.backup.api

interface BackupApi {
    suspend fun tryStartAutoBackup()
}