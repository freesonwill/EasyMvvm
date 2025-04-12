plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "arch.cayenne.lib.res"
    compileSdk = 34
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}
