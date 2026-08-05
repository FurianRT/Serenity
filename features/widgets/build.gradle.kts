plugins {
    id("convention.android")
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.furianrt.wodgets"
}

dependencies {
    implementation(projects.libraries.uikit)
    implementation(projects.libraries.common)
    implementation(projects.libraries.domain)

    implementation(libs.lifecycle)

    implementation(libs.glanceWidget)
    implementation(libs.glanceMaterial3)

    implementation(libs.hilt)
    implementation(libs.hiltNavigation)
    ksp(libs.hiltCompiler)
    ksp(libs.hiltAndroidCompiler)
}
