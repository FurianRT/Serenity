plugins {
    id("convention.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.furianrt.storage"
}

dependencies {
    implementation(Deps.corrutinesCore)
    implementation(Deps.corrutinesAndroid)

    implementation(Deps.room)
    implementation(Deps.roomRuntime)
    kapt(Deps.roomCompiler)

    implementation(Deps.hilt)
    implementation(Deps.hiltNavigation)
    kapt(Deps.hiltCompiler)
}
