import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

apply(from = rootProject.file("gradle/_sign.gradle"))

// 读取 local.properties
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

android {
    namespace = "arch.cayenne.lib.perf"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        localProperties.getOrDefault("PERF_FPS_MONITOR", "false").let { it as String
            buildConfigField("Boolean", "PERF_FPS_MONITOR", it)
        }
        localProperties.getOrDefault("PERF_MEMORY_MONITOR", "false").let { it as String
            buildConfigField("Boolean", "PERF_MEMORY_MONITOR", it)
        }
        localProperties.getOrDefault("PERF_FRAME_DROP_MONITOR", "true").let { it as String
            buildConfigField("Boolean", "PERF_FRAME_DROP_MONITOR", it)
        }

    }
    buildFeatures {
        buildConfig = true
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
}

dependencies {
    implementation(project(":lib_base"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}