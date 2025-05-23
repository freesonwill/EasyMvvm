plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}
apply(from = rootProject.file("gradle/_sign.gradle"))
apply(from = rootProject.file("gradle/flavor.gradle"))

android {
    namespace = "arch.cayenne.lib.res"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":lib_base"))
    implementation(project(":lib_common"))
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}
