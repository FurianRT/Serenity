package com.furianrt.domain.voice

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
import javax.inject.Inject

private const val PROGRESS_LOOP_STEP = 50L

interface AudioPlayerListener {
    fun onAudioProgressChange(progress: Float)
    fun onAudioPlayComplete()
}

class AudioPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
    dispatchers: DispatchersProvider,
) {
    private val scope = CoroutineScope(dispatchers.io + SupervisorJob())

    private var player: MediaPlayer? = null
    private var progressListener: AudioPlayerListener? = null
    private var progressJob: Job? = null

    private val onCompletionListener = MediaPlayer.OnCompletionListener {
        progressListener?.onAudioPlayComplete()
    }

    fun play(uri: Uri, progress: Float) {
        MediaPlayer.create(context, uri).apply {
            player = this
            setProgress(progress)
            setOnCompletionListener(onCompletionListener)
            start()
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
        val duration = player?.duration
        if (duration != null) {
            player?.seekTo((duration * progress).toLong(), MediaPlayer.SEEK_NEXT_SYNC)
        }
    }

    fun setProgressListener(listener: AudioPlayerListener) {
        progressListener = listener
    }

    fun clearProgressListener() {
        progressListener = null
    }

    private fun startProgressLoop() {
        if (progressJob == null) {
            progressJob = scope.launch {
                while (true) {
                    delay(PROGRESS_LOOP_STEP)
                    val currentPosition = player?.currentPosition
                    val duration = player?.duration
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