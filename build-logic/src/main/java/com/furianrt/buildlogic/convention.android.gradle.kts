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
        val properties = Properties().apply { load(localProperties.inputStream()) }
        defaultConfig {
            if (file("${project.name}-proguard-rules.pro").exists()) {
                consumerProguardFiles("${project.name}-proguard-rules.pro")
            }
            buildConfigField("String", "PREFS_PASSWORD", "\"${properties.getProperty("PREFS_PASSWORD")}\"")
            buildConfigField("String", "GMAIL_APP_PASSWORD", "\"${properties.getProperty("GMAIL_APP_PASSWORD")}\"")
            buildConfigField("String", "SUPPORT_EMAIL", "\"${properties.getProperty("SUPPORT_EMAIL")}\"")
            buildConfigField("String", "FILE_PROVIDER_AUTHORITY", "\"SerenityFileProvider\"")
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/NOTICE.md"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/DEPENDENCIES"
        }
    }
}
