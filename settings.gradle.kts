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

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")
include(":libraries:uikit")
include(":libraries:storage")
include(":features:note-view")
include(":libraries:core")
include(":features:tools-panel")
include(":features:settings")
include(":features:media-selector")
include(":libraries:permissions")
include(":features:media-view")
include(":libraries:domain")
include(":features:note-create")
include(":features:note-page")
include(":libraries:common")
include(":features:note-list:note-list")
include(":features:note-list:note-list-ui")
include(":features:lock")
include(":features:note-search")
include(":features:backup")
