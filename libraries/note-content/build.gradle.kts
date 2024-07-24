plugins {
    id("convention.android")
}

android {
    namespace = "com.furianrt.notecontent"

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    implementation(projects.libraries.core)
    implementation(projects.libraries.uikit)
    implementation(projects.libraries.storage)

    implementation(libs.coreKtx)
    implementation(libs.lifecycle)
    implementation(libs.material3)
    implementation(libs.material3Size)

    implementation(libs.composeActivity)
    implementation(libs.composeUi)
    implementation(libs.composeMaterial)
    implementation(libs.composeGraphics)
    implementation(libs.composeToolingPreview)
    debugImplementation(libs.composeTooling)

    implementation(libs.immutableCollections)

    implementation(libs.coil)

    implementation(libs.flowLayout)
}
