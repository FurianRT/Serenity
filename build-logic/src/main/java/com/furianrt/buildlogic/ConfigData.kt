package com.furianrt.buildlogic

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object ConfigData {
    const val COMPILE_SDK_VERSION = 36
    const val MIN_SDK_VERSION = 33
    const val TARGET_SDK_VERSION = 36
    const val VERSION_CODE = 22
    const val VERSION_NAME = "1.4.1"
    val JVM_TARGET = JvmTarget.JVM_17
    val JAVA_VERSION = JavaVersion.VERSION_17
}
