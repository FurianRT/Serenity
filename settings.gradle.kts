pluginManagement {

    includeBuild("build-logic")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

include(":app")
include(":libraries:uikit")
include(":features:assistant")
include(":libraries:storage")
include(":features:note-view")
include(":libraries:note-content")
include(":libraries:core")
include(":features:tools-panel")
