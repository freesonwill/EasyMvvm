plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}
apply(from = rootProject.file("gradle/flavor.gradle"))

android {
    namespace = "arch.cayenne.lib.common"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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
        viewBinding = true
    }
    sourceSets {
        getByName("main") {
            res.srcDirs(
                "src/main/res", "src/main/res-black_blue", "src/main/res-black_red",
                "src/main/res-classic", "src/main/res-white_blue", "src/main/res-white_green"
            )
        }
    }
}

dependencies {
    api(project(":lib_base"))
    api(project(":lib_skin"))
    api(project(":lib_socket"))
    api(project(":lib_database"))
    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.material)
    api(libs.androidx.activity)
    api(libs.androidx.constraintlayout)
    api(libs.autosize)
    implementation(libs.mmkv)
    implementation(libs.androidx.window)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    api(libs.glide)
    kapt(libs.glidecompiler)
}