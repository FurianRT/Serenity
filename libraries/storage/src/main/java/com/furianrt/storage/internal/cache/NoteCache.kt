package com.furianrt.storage.internal.cache

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.furianrt.common.ActivityLifecycleCallbacks
import com.furianrt.common.ErrorTracker
import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.entities.LocalNote
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val CACHE_FILE = "note_cached_content.json"

@Singleton
internal class NoteCache @Inject constructor(
    dispatchers: DispatchersProvider,
    @param:ApplicationContext private val context: Context,
    private val errorTracker: ErrorTracker,
) : ActivityLifecycleCallbacks {

    private val scope = CoroutineScope(SupervisorJob() + dispatchers.io)

    private val cache = mutableMapOf<String, List<LocalNote.Content>>()

    private val cacheFile = File(context.cacheDir, CACHE_FILE)

    init {
        (context as Application).registerActivityLifecycleCallbacks(this)
    }

    @Synchronized
    fun getNoteContent(noteId: String): List<LocalNote.Content> =
        cache[noteId].orEmpty()

    @Synchronized
    fun cacheNoteContent(
        noteId: String,
        content: List<LocalNote.Content>,
    ) {
        cache[noteId] = content
    }

    @Synchronized
    fun deleteCache(noteId: String) {
        cache.remove(noteId)
        if (cache.isNotEmpty()) {
            saveToDiskAsync()
        }
    }

    override fun onActivityCreated(
        activity: Activity,
        savedInstanceState: Bundle?,
    ) {
        if (cache.isEmpty() && savedInstanceState != null) {
            restoreFromDiskAsync()
        }
    }

    override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle,
    ) {
        if (cache.isNotEmpty()) {
            saveToDiskAsync()
        }
    }

    private fun saveToDiskAsync() {
        val snapshot = synchronized(this) { cache.toMap() }

        scope.launch {
            try {
                val json = Json.encodeToString(snapshot)
                cacheFile.writeText(json)
            } catch (e: Exception) {
                errorTracker.trackNonFatalError(e)
            }
        }
    }

    private fun restoreFromDiskAsync() {
        scope.launch {
            synchronized(this@NoteCache) {
                try {
                    if (!cacheFile.exists()) return@launch
                    val json = cacheFile.readText()
                    val restored = Json.decodeFromString<Map<String, List<LocalNote.Content>>>(json)
                    cache.clear()
                    cache.putAll(restored)
                } catch (e: Exception) {
                    cache.clear()
                    errorTracker.trackNonFatalError(e)
                }
            }
        }
    }
}
