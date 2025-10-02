import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}
apply(from = rootProject.file("gradle/flavor.gradle"))
apply(from = rootProject.file("gradle/_duplicate_color_names.gradle.kt"))

val buildTime = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
android {
    namespace = "arch.cayenne.lib.common"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "BUILD_TIME", "\"$buildTime\"")
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

}

dependencies {
    api(project(":lib_base"))
    api(project(":lib_skin"))
    api(project(":lib_websocket"))
    api(project(":lib_database"))
    api(libs.jsbridge)
    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.material)
    api(libs.androidx.activity)
    api(libs.androidx.constraintlayout)
    api(libs.autosize)
    implementation(libs.mmkv)
    implementation(libs.androidx.window)
    api(libs.pullrefresh.kernel)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.espresso.core)
    api(libs.glide)
    implementation(libs.glide.avif)
    api(libs.calendarview)
    kapt(libs.glidecompiler)
    api(libs.utilcodex)
    api(libs.gson)
    implementation(libs.brv)
}