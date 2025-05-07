plugins {
    id("convention.android")
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.furianrt.notepage"
}

dependencies {
    implementation(projects.features.toolsPanel)
    implementation(projects.features.mediaSelector)
    implementation(projects.features.noteList.noteListUi)

    implementation(projects.libraries.core)
    implementation(projects.libraries.common)
    implementation(projects.libraries.uikit)
    implementation(projects.libraries.permissions)
    implementation(projects.libraries.domain)

    implementation(libs.coreKtx)
    implementation(libs.lifecycle)
    implementation(libs.material3)

    implementation(platform(libs.composeBom))
    implementation(libs.composeRuntime)
    implementation(libs.composeActivity)
    implementation(libs.composeUi)
    implementation(libs.composeAnimation)
    implementation(libs.composeGraphics)
    implementation(libs.composeToolingPreview)
    debugImplementation(libs.composeTooling)
    implementation(libs.composeNavigation)

    implementation(libs.coroutinesCore)
    implementation(libs.coroutinesAndroid)

    implementation(libs.coil)
    implementation(libs.coilGif)

    implementation(libs.hilt)
    implementation(libs.hiltNavigation)
    ksp(libs.hiltCompiler)
    ksp(libs.hiltAndroidCompiler)

    implementation(libs.lottie)

    implementation(libs.immutableCollections)

    implementation(libs.blur)
}
