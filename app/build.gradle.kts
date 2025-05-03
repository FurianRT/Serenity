plugins {
    id("convention.android-app")
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gms)
    alias(libs.plugins.crashlyticsPlugin)
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
    implementation(projects.features.noteList.noteList)
    implementation(projects.features.security)
    implementation(projects.features.noteSearch)
    implementation(projects.features.backup)

    implementation(projects.libraries.core)
    implementation(projects.libraries.uikit)
    implementation(projects.libraries.domain)
    implementation(projects.libraries.storage)
    implementation(projects.libraries.permissions)
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

    implementation(libs.splashScreen)

    implementation(libs.coroutinesCore)
    implementation(libs.coroutinesAndroid)

    implementation(libs.hilt)
    implementation(libs.hiltNavigation)
    implementation(libs.hiltWork)
    ksp(libs.hiltCompiler)
    ksp(libs.hiltAndroidCompiler)

    implementation(libs.blur)

    implementation(libs.biometric)

    implementation(platform(libs.firebaseBom))
    implementation(libs.analytics)
    implementation(libs.crashlytics)
}
