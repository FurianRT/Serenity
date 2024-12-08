plugins {
    id("convention.android")
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.furianrt.domain"
}

dependencies {
    implementation(projects.libraries.core)
    implementation(projects.libraries.common)

    implementation(libs.coroutinesCore)

    implementation(libs.hilt)
    implementation(libs.hiltNavigation)
    ksp(libs.hiltCompiler)

    implementation(libs.kotlinxSerializationJson)
}
