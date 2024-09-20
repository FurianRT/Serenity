plugins {
    id("convention.android-app")
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.furianrt.serenity"
}

dependencies {
    implementation(projects.features.noteView)
    implementation(projects.features.noteCreate)
    implementation(projects.features.settings)
    implementation(projects.features.mediaSelector)
    implementation(projects.features.mediaView)

    implementation(projects.libraries.core)
    implementation(projects.libraries.uikit)
    implementation(projects.libraries.storage)
    implementation(projects.libraries.noteContent)
    implementation(projects.libraries.domain)

    implementation(libs.coreKtx)
    implementation(libs.lifecycle)
    implementation(libs.material3)

    implementation(libs.composeActivity)
    implementation(libs.composeFoundation)
    implementation(libs.composeUi)
    implementation(libs.composeGraphics)
    implementation(libs.navAnimation)
    implementation(libs.composeToolingPreview)
    implementation(libs.composeTooling)

    implementation(libs.splashScreen)

    implementation(libs.coroutinesCore)
    implementation(libs.coroutinesAndroid)

    implementation(libs.hilt)
    implementation(libs.hiltNavigation)
    ksp(libs.hiltCompiler)

    implementation(libs.collapsingToolbar)

    implementation(libs.immutableCollections)

    implementation(libs.blur)
}
