sealed interface Modules {
    object Libraries : Modules {
        const val uikit = ":libraries:uikit"
        const val storage = ":libraries:storage"
        const val noteContent = ":libraries:note-content"
        const val core = ":libraries:core"
    }

    object Features : Modules {
        const val assistant = ":features:assistant"
        const val noteView = ":features:note-view"
    }
}
