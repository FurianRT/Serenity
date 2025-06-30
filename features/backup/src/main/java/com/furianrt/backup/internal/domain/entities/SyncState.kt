package com.furianrt.backup.internal.domain.entities

internal sealed interface SyncState {
    data object Idle : SyncState
    data object Starting : SyncState
    data object Failure : SyncState
    data object Success : SyncState
    data class Progress(
        val syncedNotesCount: Int,
        val totalNotesCount: Int,
    ) : SyncState
}