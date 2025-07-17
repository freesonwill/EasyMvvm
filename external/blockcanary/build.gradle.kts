plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}
apply(from = rootProject.file("gradle/_sign.gradle"))
android {
    namespace = "plugin.cayenne.blockcanary"
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
}
val latestVersion = "0.0.5"

dependencies {
    //引入卡顿监控实现依赖库
    implementation("io.github.knight-zxw:blockcanary:${latestVersion}")
    //引入卡顿消息通知及相关展示UI
    implementation("io.github.knight-zxw:blockcanary-ui:${latestVersion}")
    implementation(libs.startup)
    implementation(project(":lib_base"))
}

