plugins {
    id("convention.android")
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.furianrt.backup"
}

dependencies {
    implementation(projects.libraries.core)
    implementation(projects.libraries.uikit)
    implementation(projects.libraries.domain)
    implementation(projects.libraries.common)

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

    implementation(libs.hilt)
    implementation(libs.hiltNavigation)
    implementation(libs.hiltWork)
    ksp(libs.hiltCompiler)
    ksp(libs.hiltAndroidCompiler)

    implementation(libs.dataStore)

    implementation(libs.kotlinxSerializationJson)

    implementation(libs.lottie)

    implementation(libs.blur)

    implementation(libs.googleAuth)
    implementation(libs.credentials)
    implementation(libs.googleid)
    implementation(libs.credentialsAuth)

    implementation(libs.retrofit)
    implementation(libs.loggingInterceptor)
    implementation(libs.kotlinSerializationConverter)

    implementation(libs.googleDrive)
    implementation(libs.googleDriveGms)

    implementation(libs.workManager)
}
