plugins {
    id("convention.android")
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.furianrt.domain"
}

dependencies {
    implementation(projects.libraries.core)

    implementation(libs.coroutinesCore)

    implementation(libs.hilt)
    implementation(libs.hiltNavigation)
    ksp(libs.hiltCompiler)
}
