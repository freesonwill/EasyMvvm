import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

apply(from = rootProject.file("gradle/flavor.gradle"))

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
    implementation(project(":lib_socket"))
    implementation(project(":module_home"))
    implementation(project(":module_login"))
    implementation(project(":module_setting"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.immersionbar)
}