plugins {
    id("convention.android")
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.furianrt.assistant"
}

dependencies {
    implementation(projects.features.toolsPanel)

    implementation(projects.libraries.core)
    implementation(projects.libraries.uikit)
    implementation(projects.libraries.storage)
    implementation(projects.libraries.noteContent)

    implementation(libs.coreKtx)
    implementation(libs.lifecycle)
    implementation(libs.material3)
    implementation(libs.material3Size)

    implementation(libs.composeActivity)
    implementation(libs.composeUi)
    implementation(libs.navAnimation)
    implementation(libs.composeMaterial)
    implementation(libs.composeGraphics)
    implementation(libs.composeToolingPreview)
    debugImplementation(libs.composeTooling)

    implementation(libs.coroutinesCore)
    implementation(libs.coroutinesAndroid)

    implementation(libs.hilt)
    implementation(libs.hiltNavigation)
    ksp(libs.hiltCompiler)

    implementation(libs.collapsingToolbar)

    implementation(libs.lottie)

    implementation(libs.immutableCollections)
}
