plugins {
    id("convention.android")
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.furianrt.storage"
}

dependencies {
    implementation(projects.libraries.core)
    implementation(projects.libraries.domain)

    implementation(libs.coroutinesCore)
    implementation(libs.coroutinesAndroid)

    implementation(libs.room)
    implementation(libs.roomRuntime)
    ksp(libs.roomCompiler)

    implementation(libs.hilt)
    implementation(libs.hiltNavigation)
    ksp(libs.hiltCompiler)

    implementation(libs.dataStore)

    implementation(libs.kotlinxSerializationJson)
}
