package com.furianrt.domain.voice

import android.content.Context
import android.media.MediaRecorder
import com.furianrt.core.DispatchersProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.math.min

private const val MAX_VOLUME = 32767f
private const val SAMPLING_RATE = 44100
private const val ENCODING_BIT_RATE = 128000

class AudioRecorder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatchersProvider,
) {
    private var recorder: MediaRecorder? = null

    var isRecording = false
        private set

    suspend fun start(outputFile: File): Boolean = withContext(dispatchers.io) {
        try {
            recorder = MediaRecorder(context).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(ENCODING_BIT_RATE)
                setAudioSamplingRate(SAMPLING_RATE)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }
            isRecording = true
            true
        } catch (e: Throwable) {
            e.printStackTrace()
            false
        }
    }

    suspend fun stop() = withContext(dispatchers.io) {
        try {
            recorder?.stop()
            recorder?.reset()
            recorder?.release()
            recorder = null
            isRecording = false
            true
        } catch (e: Throwable) {
            e.printStackTrace()
            isRecording = false
            false
        }
    }

    fun resume() {
        recorder?.resume()
    }

    fun pause() {
        recorder?.pause()
    }

    fun getCurrentVolume(): Float = min((recorder?.maxAmplitude ?: 0) / MAX_VOLUME, 1f)
}