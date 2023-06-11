plugins {
    id("convention.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.furianrt.assistant"

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }
}

dependencies {
    implementation(project(Modules.Libraries.storage))

    implementation(Deps.coreKtx)
    implementation(Deps.lifecycle)
    implementation(Deps.material3)
    implementation(Deps.material3Size)

    implementation(Deps.composeActivity)
    implementation(Deps.composeUi)
    implementation(Deps.composeMaterial)
    implementation(Deps.composeGraphics)
    implementation(Deps.composeToolingPreview)
    debugImplementation(Deps.composeTooling)

    implementation(Deps.corrutinesCore)
    implementation(Deps.corrutinesAndroid)

    implementation(Deps.hilt)
    implementation(Deps.hiltNavigation)
    kapt(Deps.hiltCompiler)

    implementation(Deps.lottie)
}
