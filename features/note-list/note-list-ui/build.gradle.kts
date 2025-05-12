plugins {
    id("convention.android")
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.furianrt.notelistui"
}

dependencies {
    implementation(projects.libraries.core)
    implementation(projects.libraries.uikit)
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

    implementation(libs.immutableCollections)

    implementation(libs.coil)
    implementation(libs.coilVideo)

    implementation(libs.blur)

    implementation(libs.lottie)
}
