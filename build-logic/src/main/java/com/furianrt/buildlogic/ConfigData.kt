package com.furianrt.buildlogic

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object ConfigData {
    const val COMPILE_SDK_VERSION = 36
    const val MIN_SDK_VERSION = 33
    const val TARGET_SDK_VERSION = 36
    const val VERSION_CODE = 7
    const val VERSION_NAME = "1.0.3"
    val JVM_TARGET = JvmTarget.JVM_17
    val JAVA_VERSION = JavaVersion.VERSION_17
}
