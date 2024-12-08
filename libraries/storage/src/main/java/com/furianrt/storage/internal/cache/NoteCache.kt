package com.furianrt.storage.internal.cache

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.furianrt.domain.entities.LocalNote
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private const val CONTENT_TAG = "content"

@Singleton
internal class NoteCache @Inject constructor(
    @ApplicationContext private val context: Context,
) : Application.ActivityLifecycleCallbacks {
    private var cache = mutableMapOf<String, List<LocalNote.Content>>()

    init {
        (context as Application).registerActivityLifecycleCallbacks(this)
    }

    @Synchronized
    fun getNoteContent(noteId: String): List<LocalNote.Content> = cache[noteId].orEmpty()

    @Synchronized
    fun cacheNoteContent(noteId: String, content: List<LocalNote.Content>) {
        cache[noteId] = content
    }

    @Synchronized
    fun deleteCache(noteId: String) {
        cache.remove(noteId)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        savedInstanceState?.getString(CONTENT_TAG)?.let { json ->
            cache.clear()
            cache.putAll(Json.decodeFromString(json))
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        outState.putString(CONTENT_TAG,  Json.encodeToString(cache))
    }

    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivityResumed(activity: Activity) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityDestroyed(activity: Activity) = Unit
}