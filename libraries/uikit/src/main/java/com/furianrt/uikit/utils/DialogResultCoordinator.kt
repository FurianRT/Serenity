package com.furianrt.uikit.utils

import javax.inject.Inject
import javax.inject.Singleton

sealed interface DialogResult {
    data class Ok<T>(val data: T) : DialogResult
    data object Cancel : DialogResult
}

interface DialogResultListener {
    fun onDialogResult(dialogId: Int, result: DialogResult)
}

class DialogIdentifier(
    val requestId: String,
    val dialogId: Int,
)

@Singleton
class DialogResultCoordinator @Inject constructor() {

    private val resultListeners = mutableMapOf<String, MutableList<DialogResultListener>>()

    @Synchronized
    fun addDialogResultListener(requestId: String, listener: DialogResultListener) {
        val listeners = resultListeners.getOrPut(requestId) { mutableListOf() }
        listeners.add(listener)
    }

    @Synchronized
    fun removeDialogResultListener(requestId: String, listener: DialogResultListener) {
        resultListeners[requestId]?.remove(listener)
    }

    @Synchronized
    fun onDialogResult(dialogIdentifier: DialogIdentifier, code: DialogResult) {
        resultListeners[dialogIdentifier.requestId]
            ?.forEach { it.onDialogResult(dialogIdentifier.dialogId, code) }
    }
}
