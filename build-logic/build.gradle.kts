import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
}

group = "com.furianrt.buildlogic"

dependencies {
    // Дублировать изменения версий в файл Versions модуля buildSrc
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
    implementation("com.android.tools.build:gradle:8.0.2")
}
