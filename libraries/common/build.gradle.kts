plugins {
    id("convention.android")
}

android {
    namespace = "com.furianrt.common"
}

dependencies {
    implementation(projects.libraries.core)

    implementation(libs.coreKtx)
}
