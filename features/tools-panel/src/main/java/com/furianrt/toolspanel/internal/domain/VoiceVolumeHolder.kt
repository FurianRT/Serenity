package com.furianrt.toolspanel.internal.domain

import javax.inject.Inject
import kotlin.random.Random

private const val MAX_BARS_COUNT = 42

internal class VoiceVolumeHolder @Inject constructor() {

    private val volumeList = mutableListOf<Float>()
    private val volumeBuffer = mutableListOf<Float>()
    private val coastBuffer = mutableListOf<Float>()
    private var coast = 1

    fun add(volume: Float) {
        coastBuffer.add(volume)

        if (coastBuffer.count() < coast) {
            return
        }

        val averageCoast = coastBuffer.average().toFloat()
        coastBuffer.clear()

        if (volumeList.count() < MAX_BARS_COUNT) {
            volumeList.add(averageCoast)
            return
        }

        volumeBuffer.add(averageCoast)
        if (volumeBuffer.count() < MAX_BARS_COUNT) {
            return
        }

        compressList()
    }

    fun getVolume(): List<Float> {
        val resultList = volumeList.toMutableList()
        resultList.addAll(volumeBuffer)

        if (coastBuffer.isNotEmpty()) {
            resultList.add(coastBuffer.average().toFloat())
        }

        if (resultList.count() < MAX_BARS_COUNT) {
            resultList.fillList()
            return resultList
        }

        val difference = resultList.count() - MAX_BARS_COUNT
        if (difference < 4) {
            return resultList
        }

        return resultList
            .chunked(resultList.count() / difference)
            .flatMap { chunk ->
                val min = chunk.minOrNull() ?: chunk.first()
                chunk.toMutableList().apply { remove(min) }
            }
    }

    fun clear() {
        volumeList.clear()
        volumeBuffer.clear()
        coastBuffer.clear()
        coast = 1
    }

    private fun MutableList<Float>.fillList() {
        val itemsNeeded = MAX_BARS_COUNT - count()
        repeat(itemsNeeded) {
            if (isEmpty()) {
                add(0f)
            } else {
                add(Random.nextInt(lastIndex.coerceAtLeast(1)), 0f)
            }
        }
    }

    private fun compressList() {
        val result = (volumeList + volumeBuffer)
            .chunked(2)
            .flatMap { chunk ->
                val min = chunk.minOrNull() ?: chunk.first()
                chunk.toMutableList().apply { remove(min) }
            }
        volumeList.clear()
        volumeList.addAll(result)
        volumeBuffer.clear()
        coast++
    }
}