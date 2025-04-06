import com.furianrt.buildlogic.ConfigData
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.furianrt.serenity"
    compileSdk = ConfigData.COMPILE_SDK_VERSION

    defaultConfig {
        applicationId = "com.furianrt.serenity"
        minSdk = ConfigData.MIN_SDK_VERSION
        targetSdk = ConfigData.TARGET_SDK_VERSION
        versionCode = ConfigData.VERSION_CODE
        versionName = ConfigData.VERSION_NAME

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        val localProperties = rootProject.file("local.properties")
        val properties = Properties().apply { load(localProperties.inputStream()) }
        defaultConfig {
            buildConfigField("String", "PREFS_PASSWORD", "\"${properties.getProperty("PREFS_PASSWORD")}\"")
            buildConfigField("String", "GMAIL_APP_PASSWORD", "\"${properties.getProperty("GMAIL_APP_PASSWORD")}\"")
            buildConfigField("String", "SUPPORT_EMAIL", "\"${properties.getProperty("SUPPORT_EMAIL")}\"")
            buildConfigField("String", "OAUTH_CLIENT_ID", "\"${properties.getProperty("OAUTH_CLIENT_ID")}\"")
        }

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
