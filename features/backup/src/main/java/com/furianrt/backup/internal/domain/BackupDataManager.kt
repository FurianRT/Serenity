package com.furianrt.backup.internal.domain

import com.furianrt.backup.internal.domain.entities.SyncState
import com.furianrt.backup.internal.domain.entities.RemoteFile
import com.furianrt.backup.internal.domain.repositories.BackupRepository
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.domain.repositories.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BackupDataManager @Inject constructor(
    private val notesRepository: NotesRepository,
    private val backupRepository: BackupRepository,
    private val mediaRepository: MediaRepository,
) {
    private val progressState: MutableStateFlow<SyncState> = MutableStateFlow(SyncState.Idle)
    val state = progressState.asStateFlow()

    suspend fun startBackup() {
        progressState.update { SyncState.Starting }

        val remoteFiles = backupRepository.getContentList()
            .onFailure { it.printStackTrace() }
            .getOrNull()

        if (remoteFiles == null) {
            progressState.update { SyncState.Failure }
            return
        }

        if (!deleteUnusedMediaFiles(remoteFiles)) {
            progressState.update { SyncState.Failure }
            return
        }

        if (!uploadNotesData(remoteFiles)) {
            progressState.update { SyncState.Failure }
            return
        }

        if (!uploadNotesMediaFiles(remoteFiles)) {
            progressState.update { SyncState.Failure }
            return
        }

        backupRepository.setLastSyncDate(ZonedDateTime.now())

        progressState.update { SyncState.Idle }
    }

    private suspend fun deleteUnusedMediaFiles(remoteFiles: List<RemoteFile>): Boolean {
        val remoteMediaFiles = remoteFiles.filter { it !is RemoteFile.NotesData }
        val localMediaFilesIds = getLocalFilesIds()
        val remoteFilesToDelete = remoteMediaFiles.filter { !localMediaFilesIds.contains(it.name) }
        return backupRepository.deleteFiles(remoteFilesToDelete)
            .onFailure { it.printStackTrace() }
            .map { true }
            .getOrDefault(false)
    }

    private suspend fun getLocalFilesIds(): Set<String> {
        val mediaIds = mediaRepository.getAllMedia().first().map(LocalNote.Content.Media::name)
        val voicesIds = mediaRepository.getAllVoices().first().map(LocalNote.Content.Voice::id)
        return buildSet {
            addAll(mediaIds)
            addAll(voicesIds)
        }
    }

    private suspend fun uploadNotesData(remoteFiles: List<RemoteFile>): Boolean {
        val notes = notesRepository.getAllNotes().first()
        backupRepository.uploadNotesData(notes)
            .onFailure { error ->
                error.printStackTrace()
                return false
            }

        val oldNotesData = remoteFiles.filterIsInstance<RemoteFile.NotesData>()
        backupRepository.deleteFiles(oldNotesData)
            .onFailure { it.printStackTrace() }

        return true
    }

    private suspend fun uploadNotesMediaFiles(remoteFiles: List<RemoteFile>): Boolean {
        val notes = notesRepository.getAllNotes().first()
        progressState.update {
            SyncState.Progress(syncedNotesCount = 0, totalNotesCount = notes.count())
        }
        notes.forEachIndexed { index, note ->
            if (uploadNotesMediaFiles(note, remoteFiles)) {
                progressState.update {
                    SyncState.Progress(
                        syncedNotesCount = index + 1,
                        totalNotesCount = notes.count(),
                    )
                }
            } else {
                return false
            }
        }
        return true
    }

    private suspend fun uploadNotesMediaFiles(
        note: LocalNote,
        remoteFiles: List<RemoteFile>,
    ): Boolean {
        for (content in note.content) {
            when (content) {
                is LocalNote.Content.Voice -> {
                    if (remoteFiles.any { it.name == content.id }) {
                        continue
                    }
                    backupRepository.uploadVoice(content)
                        .onFailure { error ->
                            error.printStackTrace()
                            return false
                        }
                }

                is LocalNote.Content.MediaBlock -> content.media.forEach { media ->
                    if (remoteFiles.any { it.name == media.name }) {
                        return@forEach
                    }
                    when (media) {
                        is LocalNote.Content.Image -> backupRepository.uploadMedia(media)
                            .onFailure { error ->
                                error.printStackTrace()
                                return false
                            }

                        is LocalNote.Content.Video -> backupRepository.uploadMedia(media)
                            .onFailure { error ->
                                error.printStackTrace()
                                return false
                            }
                    }
                }

                is LocalNote.Content.Title -> Unit
            }
        }
        return true
    }


    /* private suspend fun backupAllNotesData(
         notes: List<LocalNote>,
     ): Boolean = if (backupNotesJson(notes)) {
         notes.forEachIndexed { index, note ->
             if (backupNoteFiles(note)) {
                 progressState.update {
                     BackupState.Progress(value = index.toFloat() / notes.count())
                 }
             } else {
                 return false
             }
         }
         true
     } else {
         false
     }
 */


    /* private suspend fun backupNoteFiles(note: LocalNote): Boolean {

         driveApiService.createFolder(folderMetadata = buildFolderMetadata(note.id))

         note.content.forEach { content ->
             when (content) {
                 is LocalNote.Content.Voice -> {
                     if (!backupVoice(note.id, content)) {
                         return false
                     }
                 }

                 is LocalNote.Content.MediaBlock -> {
                     content.media.forEach { media ->
                         val isSuccess = when (media) {
                             is LocalNote.Content.Image -> backupImage(note.id, media)
                             is LocalNote.Content.Video -> backupVideo(note.id, media)
                         }
                         if (!isSuccess) {
                             return false
                         }
                     }
                 }

                 is LocalNote.Content.Title -> Unit
             }
         }
         return true
     }*/
}