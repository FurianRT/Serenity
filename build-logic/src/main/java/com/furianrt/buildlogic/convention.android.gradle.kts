import com.furianrt.buildlogic.ConfigData

plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = ConfigData.COMPILE_SDK_VERSION

    defaultConfig {
        minSdk = ConfigData.MIN_SDK_VERSION
    }

    buildTypes {
        defaultConfig {
            if (file("${project.name}-proguard-rules.pro").exists()) {
                consumerProguardFiles("${project.name}-proguard-rules.pro")
            }
        }
        release {
            isMinifyEnabled = false
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
        jvmTarget = ConfigData.JVM_TARGET
    }
}
