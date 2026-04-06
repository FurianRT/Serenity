package com.furianrt.backup.internal.extensions

import com.furianrt.backup.internal.data.remote.google.drive.DriveFilesListResponse
import com.furianrt.backup.internal.domain.entities.PopularQuestion
import com.furianrt.backup.internal.domain.entities.RemoteFile
import com.furianrt.backup.internal.ui.BackupUiState.Content
import com.furianrt.backup.internal.ui.entities.Question
import com.furianrt.uikit.extensions.toDateString
import java.time.Instant
import java.time.LocalDate
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

    mimeType == "application/json" && name == RemoteFile.NoteBackgroundsData.FILE_NAME -> {
        RemoteFile.NoteBackgroundsData(
            id = id,
            createdAt = Instant.parse(createdTime),
        )
    }

    mimeType == "application/json" && name == RemoteFile.NotesData.FILE_NAME -> {
        RemoteFile.NotesData(
            id = id,
            name = name,
            createdAt = Instant.parse(createdTime),
        )
    }

    else -> null
}

internal fun ZonedDateTime?.toSyncDate(): Content.Success.SyncDate {
    val localDateNow = LocalDate.now()
    val localDate = this?.toLocalDate()
    return when {
        this == null || localDate == null -> Content.Success.SyncDate.None
        localDateNow == localDate -> Content.Success.SyncDate.Today
        localDateNow.minusDays(1) == localDate -> Content.Success.SyncDate.Yesterday
        else -> Content.Success.SyncDate.Other(
            text = this.toDateString(LAST_SYNC_DATE_PATTERN),
        )
    }
}
