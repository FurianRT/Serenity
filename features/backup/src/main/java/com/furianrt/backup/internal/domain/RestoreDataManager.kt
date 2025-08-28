package com.furianrt.backup.internal.domain

import com.furianrt.backup.internal.domain.entities.RemoteFile
import com.furianrt.backup.internal.domain.entities.SyncState
import com.furianrt.backup.internal.domain.repositories.BackupRepository
import com.furianrt.common.ErrorTracker
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.SimpleNote
import com.furianrt.domain.repositories.DeviceInfoRepository
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.domain.usecase.UpdateNoteContentUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class RestoreDataManager @Inject constructor(
    private val notesRepository: NotesRepository,
    private val backupRepository: BackupRepository,
    private val mediaRepository: MediaRepository,
    private val updateNoteContentUseCase: UpdateNoteContentUseCase,
    private val deviceInfoRepository: DeviceInfoRepository,
    private val errorTracker: ErrorTracker,
) {
    private val progressState: MutableStateFlow<SyncState> = MutableStateFlow(SyncState.Idle)
    val state = progressState.asStateFlow()

    fun clearFailureState() {
        if (state.value is SyncState.Failure) {
            progressState.update { SyncState.Idle }
        }
    }

    suspend fun startRestore() {
        if (state.value is SyncState.Progress || state.value is SyncState.Starting) {
            return
        }

        if (!deviceInfoRepository.hasNetworkConnection()) {
            progressState.update { SyncState.Failure }
            return
        }

        progressState.update { SyncState.Starting }

        val remoteFiles = backupRepository.getContentList()
            .onFailure(errorTracker::trackNonFatalError)
            .getOrNull()

        if (remoteFiles == null) {
            progressState.update { SyncState.Failure }
            return
        }

        val notesData = remoteFiles
            .filterIsInstance<RemoteFile.NotesData>()
            .maxByOrNull(RemoteFile.NotesData::createdAt)

        if (notesData == null) {
            progressState.update { SyncState.Idle }
            return
        }

        val remoteNotes = backupRepository.getRemoteNotes(notesData.id)
            .onFailure(errorTracker::trackNonFatalError)
            .getOrNull()

        if (remoteNotes == null) {
            progressState.update { SyncState.Failure }
            return
        }

        if (remoteNotes.isEmpty()) {
            progressState.update { SyncState.Idle }
            return
        }

        val localNotes = notesRepository.getAllNotes().first()

        if (!syncNotesMedia(remoteFiles, localNotes, remoteNotes)) {
            progressState.update { SyncState.Failure }
            return
        }

        saveNotesData(remoteNotes)

        backupRepository.setLastSyncDate(ZonedDateTime.now())

        progressState.update { SyncState.Success }
        delay(500)
        progressState.update { SyncState.Idle }
    }

    private suspend fun syncNotesMedia(
        remoteFiles: List<RemoteFile>,
        localNotes: List<LocalNote>,
        remoteNotes: List<LocalNote>,
    ): Boolean {
        progressState.update {
            SyncState.Progress(
                syncedNotesCount = 0,
                totalNotesCount = remoteNotes.count(),
            )
        }
        remoteNotes.forEachIndexed { index, remoteNote ->
            val isSuccess = saveNoteMedia(
                remoteFiles = remoteFiles,
                localNote = localNotes.find { it.id == remoteNote.id },
                remoteNote = remoteNote,
            )
            if (isSuccess) {
                saveNotesData(listOf(remoteNote))
                progressState.update {
                    SyncState.Progress(
                        syncedNotesCount = index + 1,
                        totalNotesCount = remoteNotes.count(),
                    )
                }
            } else {
                return false
            }
        }
        return true
    }

    private suspend fun saveNoteMedia(
        remoteFiles: List<RemoteFile>,
        localNote: LocalNote?,
        remoteNote: LocalNote,
    ): Boolean {
        val localMedia = localNote?.content
            ?.filterIsInstance<LocalNote.Content.MediaBlock>()
            ?.flatMap(LocalNote.Content.MediaBlock::media)
            .orEmpty()
        val localVoices = localNote?.content
            ?.filterIsInstance<LocalNote.Content.Voice>()
            .orEmpty()

        val remoteMedia = remoteNote.content
            .filterIsInstance<LocalNote.Content.MediaBlock>()
            .flatMap(LocalNote.Content.MediaBlock::media)
        val remoteVoices = remoteNote.content
            .filterIsInstance<LocalNote.Content.Voice>()

        val mediaToSave = remoteMedia
            .filter { remote -> localMedia.none { it.id == remote.id } }
        val voicesToSave = remoteVoices
            .filter { remote -> localVoices.none { it.id == remote.id } }

        voicesToSave.forEach { voice ->
            if (!syncNoteVoiceFile(remoteNote.id, voice, remoteFiles)) {
                return false
            }
        }

        mediaToSave.forEach { media ->
            if (!syncNoteMediaFile(remoteNote.id, media, remoteFiles)) {
                return false
            }
        }

        return true
    }

    private suspend fun syncNoteMediaFile(
        noteId: String,
        media: LocalNote.Content.Media,
        remoteFiles: List<RemoteFile>,
    ): Boolean {
        val remoteFileId = remoteFiles.find { it.name == media.id }?.id ?: return true
        val localFile = mediaRepository.createMediaDestinationFile(noteId, media.id, media.name)
            ?: return false
        return backupRepository.loadRemoteLocalToFile(remoteFileId, localFile)
            .onFailure(errorTracker::trackNonFatalError)
            .map { true }
            .getOrDefault(false)
    }

    private suspend fun syncNoteVoiceFile(
        noteId: String,
        voice: LocalNote.Content.Voice,
        remoteFiles: List<RemoteFile>,
    ): Boolean {
        val remoteFileId = remoteFiles.find { it.name == voice.id }?.id ?: return true
        val localFile = mediaRepository.createVoiceDestinationFile(noteId, voice.id)
            ?: return false
        return backupRepository.loadRemoteLocalToFile(remoteFileId, localFile)
            .onFailure(errorTracker::trackNonFatalError)
            .map { true }
            .getOrDefault(false)
    }

    private suspend fun saveNotesData(remoteNotes: List<LocalNote>) {
        remoteNotes.forEach { note ->
            notesRepository.upsertNote(
                note = SimpleNote(
                    id = note.id,
                    font = note.fontFamily,
                    fontColor = note.fontColor,
                    fontSize = note.fontSize,
                    backgroundId = note.backgroundId,
                    moodId = note.moodId,
                    date = note.date,
                    isPinned = note.isPinned,
                )
            )
            updateNoteContentUseCase(
                noteId = note.id,
                content = note.content,
                tags = note.tags,
                stickers = note.stickers,
                fontFamily = note.fontFamily,
                fontColor = note.fontColor,
                fontSize = note.fontSize,
                backgroundId = note.backgroundId,
                moodId = note.moodId,
                noteLocation = note.location,
                updateMediaFiles = false,
            )
        }
    }
}