package com.furianrt.storage.internal.managers

import com.furianrt.core.DispatchersProvider
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.internal.database.notes.dao.ImageDao
import com.furianrt.storage.internal.database.notes.dao.VideoDao
import com.furianrt.storage.internal.device.AppMediaStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class MediaSaver @Inject constructor(
    dispatchers: DispatchersProvider,
    private val imageDao: ImageDao,
    private val videoDao: VideoDao,
    private val appMediaStorage: AppMediaStorage,
) {
    private val scope = CoroutineScope(dispatchers.io + SupervisorJob())


    @Synchronized
    fun save(noteId: String, media: List<LocalNote.Content.Media>) {

    }

    @Synchronized
    fun cancel(noteId: String, media: LocalNote.Content.Media) {

    }
}