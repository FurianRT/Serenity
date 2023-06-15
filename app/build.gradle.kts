plugins {
    id("convention.android-app")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.furianrt.serenity"

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }
}

dependencies {
    implementation(project(Modules.Features.assistant))
    implementation(project(Modules.Features.noteView))

    implementation(project(Modules.Libraries.uikit))
    implementation(project(Modules.Libraries.storage))
    implementation(project(Modules.Libraries.noteContent))

    implementation(Deps.coreKtx)
    implementation(Deps.lifecycle)
    implementation(Deps.material3)
    implementation(Deps.material3Size)

    implementation(Deps.composeActivity)
    implementation(Deps.composeFoundation)
    implementation(Deps.composeUi)
    implementation(Deps.composeMaterial)
    implementation(Deps.composeGraphics)
    implementation(Deps.composeToolingPreview)
    debugImplementation(Deps.composeTooling)

    implementation(Deps.splashScreen)

    implementation(Deps.coroutinesCore)
    implementation(Deps.coroutinesAndroid)

    implementation(Deps.hilt)
    implementation(Deps.hiltNavigation)
    kapt(Deps.hiltCompiler)

    implementation(Deps.collapsingToolbar)

    implementation(Deps.immutableCollections)

    implementation(Deps.navAnimation)
}
