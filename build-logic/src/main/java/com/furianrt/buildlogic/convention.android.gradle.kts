import java.util.Properties
import com.android.build.api.variant.BuildConfigField

plugins {
    id("com.android.library")
}

val rootDirFile = project.isolated.rootProject.projectDirectory
val localPropertiesFile = rootDirFile.file("local.properties")

val prefsPassword = providers.fileContents(localPropertiesFile).asText.map { text ->
    val props = Properties().apply { load(text.reader()) }
    props.getProperty("PREFS_PASSWORD")
}

val gmailPassword = providers.fileContents(localPropertiesFile).asText.map { text ->
    val props = Properties().apply { load(text.reader()) }
    props.getProperty("GMAIL_APP_PASSWORD")
}

val supportEmail = providers.fileContents(localPropertiesFile).asText.map { text ->
    val props = Properties().apply { load(text.reader()) }
    props.getProperty("SUPPORT_EMAIL")
}

android {
    compileSdk = 37

    defaultConfig {
        minSdk = 33
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        defaultConfig {
            val proguardFile = layout.projectDirectory.file("${name}-proguard-rules.pro").asFile
            if (proguardFile.exists()) {
                consumerProguardFiles(proguardFile.name)
            }

            // Статический BuildConfigField можно оставить здесь
            buildConfigField("String", "FILE_PROVIDER_AUTHORITY", "\"SerenityFileProvider\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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

androidComponents {
    onVariants { variant ->
        variant.buildConfigFields?.put(
            "PREFS_PASSWORD",
            prefsPassword.map { BuildConfigField("String", "\"$it\"", null) }
        )
        variant.buildConfigFields?.put(
            "GMAIL_APP_PASSWORD",
            gmailPassword.map { BuildConfigField("String", "\"$it\"", null) }
        )
        variant.buildConfigFields?.put(
            "SUPPORT_EMAIL",
            supportEmail.map { BuildConfigField("String", "\"$it\"", null) }
        )
    }
}
