plugins {
    id("convention.android")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.furianrt.common"
}

dependencies {
    implementation(projects.libraries.core)

    implementation(libs.coreKtx)

    implementation(libs.kotlinxSerializationJson)
}
