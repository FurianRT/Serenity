sealed interface Modules {
    object Libraries : Modules {
        const val uikit = ":libraries:uikit"
        const val storage = ":libraries:storage"
    }

    object Features : Modules {
        const val assistant = ":features:assistant"
        const val noteView = ":features:noteview"
    }
}