plugins {
    id("convention.android")
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.furianrt.mediaselector"
}

dependencies {
    implementation(projects.libraries.core)
    implementation(projects.libraries.common)
    implementation(projects.libraries.uikit)
    implementation(projects.libraries.domain)
    implementation(projects.libraries.permissions)

    implementation(libs.coreKtx)
    implementation(libs.lifecycle)
    implementation(libs.material3)

    implementation(libs.composeActivity)
    implementation(libs.composeFoundation)
    implementation(libs.composeUi)
    implementation(libs.composeGraphics)
    implementation(libs.composeToolingPreview)
    implementation(libs.composeTooling)
    implementation(libs.composeNavigation)

    implementation(libs.coroutinesCore)
    implementation(libs.coroutinesAndroid)

    implementation(libs.hilt)
    implementation(libs.hiltNavigation)
    ksp(libs.hiltCompiler)

    implementation(libs.immutableCollections)

    implementation(libs.coil)
    implementation(libs.coilVideo)

    implementation(libs.permissions)

    implementation(libs.blur)

    implementation(libs.kotlinxSerializationJson)
}
