// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version Versions.android apply false
    id("com.android.library") version Versions.android apply false
    kotlin("android") version Versions.kotlin apply false
    kotlin("kapt") version Versions.kotlin apply false
    id("com.google.dagger.hilt.android") version Versions.hilt apply false
}
