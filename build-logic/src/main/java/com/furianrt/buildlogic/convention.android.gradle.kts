import com.furianrt.buildlogic.ConfigData
import java.util.Properties

plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = ConfigData.COMPILE_SDK_VERSION

    defaultConfig {
        minSdk = ConfigData.MIN_SDK_VERSION
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        val localProperties = rootProject.file("local.properties")
        val prefsPassword = Properties().apply {
            load(localProperties.inputStream())
        }.getProperty("PREFS_PASSWORD")

        defaultConfig {
            if (file("${project.name}-proguard-rules.pro").exists()) {
                consumerProguardFiles("${project.name}-proguard-rules.pro")
            }
            buildConfigField("String", "PREFS_PASSWORD", "\"${prefsPassword}\"")
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
