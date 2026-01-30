package com.furianrt.buildlogic

import org.gradle.api.JavaVersion

object ConfigData {
    const val COMPILE_SDK_VERSION = 36
    const val MIN_SDK_VERSION = 33
    const val TARGET_SDK_VERSION = 36

    const val VERSION_CODE = 30
    const val VERSION_NAME = "1.8.1"

    val JAVA_VERSION = JavaVersion.VERSION_17
}
