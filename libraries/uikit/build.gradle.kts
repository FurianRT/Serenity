plugins {
    id("convention.android")
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.furianrt.uikit"
}

dependencies {
    implementation(projects.libraries.core)

    implementation(libs.coreKtx)
    implementation(libs.lifecycle)
    implementation(libs.material3)
    implementation(libs.material3Size)

    implementation(libs.composeActivity)
    implementation(libs.composeFoundation)
    implementation(libs.composeUi)
    implementation(libs.composeMaterial)
    implementation(libs.composeGraphics)
    implementation(libs.composeToolingPreview)
    debugImplementation(libs.composeTooling)

    implementation(libs.collapsingToolbar)
}
