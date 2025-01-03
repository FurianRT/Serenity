package com.furianrt.toolspanel.internal.domain

import android.content.Context
import android.media.MediaRecorder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class VoiceRecorder @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private var recorder: MediaRecorder? = null

    var isRecording = false
        private set

    fun start(outputFile: File): Boolean = try {
        recorder = MediaRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(FileOutputStream(outputFile).fd)
            prepare()
            start()
        }
        isRecording = true
        true
    } catch (e: Throwable) {
        e.printStackTrace()
        false
    }

    fun stop() = try {
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

    fun resume() {
        recorder?.resume()
    }

    fun pause() {
        recorder?.pause()
    }
}