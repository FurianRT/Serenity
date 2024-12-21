plugins {
    id("convention.android")
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.furianrt.toolspanel"
}

dependencies {
    implementation(projects.libraries.core)
    implementation(projects.libraries.uikit)
    implementation(projects.libraries.permissions)

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

    implementation(libs.blur)
}
