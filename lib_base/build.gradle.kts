plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.coolone.lib_base"
    compileSdk = BuildConfigs.compileSdk

    defaultConfig {
        minSdk = BuildConfigs.minSdk
        targetSdk = BuildConfigs.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles (
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(Dependencies.core_ktx)
    implementation(Dependencies.appcompat)
    implementation(Dependencies.material)
    implementation(Dependencies.immersionbar)
    api(project(Dependencies.lib_ui))
    testImplementation(Dependencies.junit)
    androidTestImplementation(Dependencies.ext_junit)
    androidTestImplementation(Dependencies.espresso_core)
}