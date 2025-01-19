package com.furianrt.notelistui.composables.title

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange

internal class UndoRedoEntry(
    val annotatedString: AnnotatedString,
    val selection: TextRange,
)

internal data class UndoRedoOperation(
    val preText: AnnotatedString,
    val postText: AnnotatedString,
    val preSelection: TextRange,
    val postSelection: TextRange,
    val time: Long = System.currentTimeMillis(),
)

internal class UndoRedoManager {

    companion object {
        private const val CAPACITY = 20
        private const val INTERVAL = 1000L
    }

    private var undoStack = SnapshotStateList<UndoRedoOperation>()
    private var redoStack = SnapshotStateList<UndoRedoOperation>()

    val canUndo: Boolean
        get() = undoStack.isNotEmpty()

    val canRedo: Boolean
        get() = redoStack.isNotEmpty()

    fun record(operation: UndoRedoOperation) {
        redoStack.clear()

        val lastItem = undoStack.lastOrNull()

        if (lastItem == null || operation.time - lastItem.time > INTERVAL) {
            undoStack.add(operation)
        } else {
            undoStack[undoStack.lastIndex] = lastItem.copy(
                postText = operation.postText,
                postSelection = operation.postSelection,
            )
        }

        while (undoStack.size >= CAPACITY) {
            undoStack.removeFirstOrNull()
        }
    }

    fun undo(): UndoRedoEntry? = undoStack.removeLastOrNull()?.let { operation ->
        redoStack.add(operation)
        UndoRedoEntry(
            annotatedString = operation.preText,
            selection = operation.preSelection,
        )
    }

    fun redo(): UndoRedoEntry? = redoStack.removeLastOrNull()?.let { operation ->
        undoStack.add(operation)
        UndoRedoEntry(
            annotatedString = operation.postText,
            selection = operation.postSelection,
        )
    }

    fun clearHistory() {
        undoStack.clear()
        redoStack.clear()
    }
}