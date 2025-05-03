package com.furianrt.backup.internal.extensions

import com.furianrt.backup.internal.data.remote.google.drive.DriveFilesListResponse
import com.furianrt.backup.internal.domain.entities.PopularQuestion
import com.furianrt.backup.internal.domain.entities.RemoteFile
import com.furianrt.backup.internal.ui.BackupUiState
import com.furianrt.backup.internal.ui.entities.Question
import com.furianrt.uikit.extensions.toDateString
import java.time.Instant
import java.time.ZonedDateTime

private const val LAST_SYNC_DATE_PATTERN = "dd/MM/yyyy hh:mm a"

internal fun PopularQuestion.toQuestion(isExpanded: Boolean) = Question(
    id = id,
    title = title,
    answer = answer,
    isExpanded = isExpanded,
)

internal fun DriveFilesListResponse.File.toRemoteFile() = when {
    mimeType.contains("image/") -> RemoteFile.Image(
        id = id,
        name = name,
    )

    mimeType.contains("video/") -> RemoteFile.Video(
        id = id,
        name = name,
    )

    mimeType.contains("audio/") -> RemoteFile.Voice(
        id = id,
        name = name,
    )

    mimeType == "application/json" -> RemoteFile.NotesData(
        id = id,
        name = name,
        createdAt = Instant.parse(createdTime),
    )

    else -> null
}

internal fun ZonedDateTime?.toSyncDate(): BackupUiState.Success.SyncDate {
    val localDateNow = ZonedDateTime.now()
    return when {
        this == null -> BackupUiState.Success.SyncDate.None
        localDateNow == this -> BackupUiState.Success.SyncDate.Today
        localDateNow.minusDays(1) == this -> BackupUiState.Success.SyncDate.Yesterday
        else -> BackupUiState.Success.SyncDate.Other(
            text = this.toDateString(LAST_SYNC_DATE_PATTERN),
        )
    }
}
