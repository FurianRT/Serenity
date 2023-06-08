plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.furianrt.uikit"
    compileSdk = ConfigData.compileSdkVersion

    defaultConfig {
        minSdk = ConfigData.minSdkVersion
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = ConfigData.jvmTarget
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }
}

dependencies {
    implementation(project(Libraries.storage))

    implementation(Deps.coreKtx)
    implementation(Deps.lifecycle)
    implementation(Deps.material3)
    implementation(Deps.material3Size)

    implementation(Deps.composeActivity)
    implementation(Deps.composeFoundation)
    implementation(Deps.composeUi)
    implementation(Deps.composeMaterial)
    implementation(Deps.composeGraphics)
    implementation(Deps.composeToolingPreview)
    debugImplementation(Deps.composeTooling)
}
