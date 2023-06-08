plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.furianrt.serenity"
    compileSdk = ConfigData.compileSdkVersion

    defaultConfig {
        applicationId = "com.furianrt.serenity"
        minSdk = ConfigData.minSdkVersion
        targetSdk = ConfigData.targetSdkVersion
        versionCode = ConfigData.versionCode
        versionName = ConfigData.versionName

        vectorDrawables {
            useSupportLibrary = true
        }
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(Features.assistant))

    implementation(project(Libraries.uikit))
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

    implementation(Deps.systemuicontroller)

    implementation(Deps.splashScreen)

    implementation(Deps.corrutinesCore)
    implementation(Deps.corrutinesAndroid)

    implementation(Deps.hilt)
    implementation(Deps.hiltNavigation)
    kapt(Deps.hiltCompiler)

    implementation(Deps.collapsingToolbar)

    implementation(Deps.lottie)
}
