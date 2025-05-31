import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

apply(from = rootProject.file("gradle/flavor.gradle"))
apply(from = rootProject.file("gradle/_sign.gradle"))

android {
    namespace = "com.walisport.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.walisport.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val prop = Properties()
        prop.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("int", "uid", prop.getProperty("user.uid"))
        buildConfigField("String", "token", prop.getProperty("user.token").let { it->"\"$it\"" })

        ndk {
            //abiFilters 'armeabi-v7a', 'x86', 'arm64-v8a', 'x86_64'
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a")) // 仅支持 arm 版本
        }
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
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("jniLibs")
        }
    }
}

dependencies {
    implementation(project(":lib_common"))
    implementation(project(":lib_websocket"))
    implementation(project(":lib_base"))
    implementation(project(":module_home"))
    implementation(project(":module_bet"))
    implementation(project(":module_login"))
    implementation(project(":module_setting"))
    implementation(project(":module_live"))
    implementation(project(":module_handicap"))
    implementation(project(":module_search"))
    implementation(project(":module_feedback"))
    implementation(project(":module_message"))
    implementation(project(":module_topup"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.immersionbar)
    debugImplementation(libs.leakcanary)
}