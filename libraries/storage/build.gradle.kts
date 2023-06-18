plugins {
    id("convention.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.furianrt.storage"
}

dependencies {
    implementation(project(Modules.Libraries.core))

    implementation(Deps.coroutinesCore)
    implementation(Deps.coroutinesAndroid)

    implementation(Deps.room)
    implementation(Deps.roomRuntime)
    kapt(Deps.roomCompiler)

    implementation(Deps.hilt)
    implementation(Deps.hiltNavigation)
    kapt(Deps.hiltCompiler)
}
