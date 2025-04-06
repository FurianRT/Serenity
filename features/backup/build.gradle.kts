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

    implementation(libs.coreKtx)
    implementation(libs.lifecycle)
    implementation(libs.material3)

    implementation(libs.composeActivity)
    implementation(libs.composeFoundation)
    implementation(libs.composeUi)
    implementation(libs.composeAnimation)
    implementation(libs.composeGraphics)
    implementation(libs.composeToolingPreview)
    implementation(libs.composeTooling)
    implementation(libs.composeNavigation)

    implementation(libs.hilt)
    implementation(libs.hiltNavigation)
    ksp(libs.hiltCompiler)

    implementation(libs.dataStore)

    implementation(libs.kotlinxSerializationJson)

    implementation(libs.immutableCollections)

    implementation(libs.lottie)

    implementation(libs.blur)

    implementation(libs.googleAuth)
    implementation(libs.credentials)
    implementation(libs.googleid)

    implementation(libs.retrofit)
    implementation(libs.loggingInterceptor)
    implementation(libs.kotlinSerializationConverter)

    implementation(libs.googleDrive)
}
