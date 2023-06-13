plugins {
    id("convention.android")
}

android {
    namespace = "com.furianrt.notecontent"

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }
}

dependencies {
    implementation(project(Modules.Libraries.storage))
    implementation(project(Modules.Libraries.uikit))

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

    implementation(Deps.immutableCollections)

    implementation(Deps.flowLayout)
}
