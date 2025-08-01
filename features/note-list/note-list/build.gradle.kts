plugins {
    id("convention.android")
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.furianrt.notelist"
}

dependencies {
    implementation(projects.features.noteList.noteListUi)

    implementation(projects.libraries.core)
    implementation(projects.libraries.uikit)
    implementation(projects.libraries.domain)
    implementation(projects.libraries.permissions)

    implementation(libs.coreKtx)
    implementation(libs.lifecycle)
    implementation(libs.material3)

    implementation(platform(libs.composeBom))
    implementation(libs.composeRuntime)
    implementation(libs.composeActivity)
    implementation(libs.composeFoundation)
    implementation(libs.composeUi)
    implementation(libs.composeAnimation)
    implementation(libs.composeGraphics)
    implementation(libs.composeToolingPreview)
    debugImplementation(libs.composeTooling)
    implementation(libs.composeNavigation)

    implementation(libs.splashScreen)

    implementation(libs.coroutinesCore)
    implementation(libs.coroutinesAndroid)

    implementation(libs.hilt)
    implementation(libs.hiltNavigation)
    ksp(libs.hiltCompiler)
    ksp(libs.hiltAndroidCompiler)

    implementation(libs.immutableCollections)

    implementation(libs.blur)

    implementation(libs.kotlinxSerializationJson)

    implementation(libs.lottie)
}
