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
    implementation(projects.features.settings)
    implementation(projects.features.mediaSelector)

    implementation(projects.libraries.core)
    implementation(projects.libraries.uikit)
    implementation(projects.libraries.storage)
    implementation(projects.libraries.noteContent)

    implementation(libs.coreKtx)
    implementation(libs.lifecycle)
    implementation(libs.material3)
    implementation(libs.material3Size)

    implementation(libs.composeActivity)
    implementation(libs.composeFoundation)
    implementation(libs.composeUi)
    implementation(libs.composeMaterial)
    implementation(libs.composeGraphics)
    implementation(libs.navAnimation)
    implementation(libs.composeToolingPreview)
    debugImplementation(libs.composeTooling)

    implementation(libs.splashScreen)

    implementation(libs.coroutinesCore)
    implementation(libs.coroutinesAndroid)

    implementation(libs.hilt)
    implementation(libs.hiltNavigation)
    ksp(libs.hiltCompiler)

    implementation(libs.collapsingToolbar)

    implementation(libs.immutableCollections)
}
