package com.furianrt.notepage.internal.ui.page

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.furianrt.core.DispatchersProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

private const val PROGRESS_LOOP_STEP = 50L

internal interface AudioPlayerListener {
    fun onAudioProgressChange(progress: Float)
    fun onAudioPlayComplete()
}

internal class AudioPlayer @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val dispatchers: DispatchersProvider,
) {
    private val scope = CoroutineScope(dispatchers.main + SupervisorJob())

    private var player: MediaPlayer? = null
    private var progressListener: AudioPlayerListener? = null
    private var progressJob: Job? = null

    private val onCompletionListener = MediaPlayer.OnCompletionListener {
        progressListener?.onAudioPlayComplete()
    }

    suspend fun play(
        uri: Uri,
        progress: Float,
    ) {
        stop()
        withContext(dispatchers.io) {
            val newPlayer = MediaPlayer.create(context, uri).apply {
                setProgress(progress)
                setOnCompletionListener(onCompletionListener)
            }
            player = newPlayer
            newPlayer.start()
        }
        startProgressLoop()
    }

    fun stop() {
        stopProgressLoop()
        player?.stop()
        player?.release()
        player = null
    }

    fun setProgress(progress: Float) {
        player?.setProgress(progress)
    }

    fun setProgressListener(listener: AudioPlayerListener) {
        progressListener = listener
    }

    fun clearProgressListener() {
        progressListener = null
    }

    private fun MediaPlayer.setProgress(progress: Float) {
        seekTo((duration * progress).toLong(), MediaPlayer.SEEK_NEXT_SYNC)
    }

    private fun startProgressLoop() {
        if (progressJob == null) {
            progressJob = scope.launch {
                while (true) {
                    delay(PROGRESS_LOOP_STEP.milliseconds)
                    val currentPlayer = player
                    val currentPosition = currentPlayer?.currentPosition
                    val duration = currentPlayer?.duration
                    if (currentPosition != null && duration != null) {
                        val progress = if (duration == 0) {
                            0f
                        } else {
                            currentPosition / duration.toFloat()
                        }
                        progressListener?.onAudioProgressChange(progress)
                    }
                }
            }
        }
    }

    private fun stopProgressLoop() {
        progressJob?.cancel()
        progressJob = null
    }
}
