package com.furianrt.backup.internal.extensions

import com.furianrt.backup.internal.data.remote.google.drive.DriveFilesListResponse
import com.furianrt.backup.internal.domain.entities.PopularQuestion
import com.furianrt.backup.internal.domain.entities.RemoteFile
import com.furianrt.backup.internal.ui.entities.Question
import java.time.Instant

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

    mimeType == "application/json" && name == RemoteFile.CustomStickersData.FILE_NAME -> {
        RemoteFile.CustomStickersData(
            id = id,
            createdAt = Instant.parse(createdTime),
        )
    }

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
