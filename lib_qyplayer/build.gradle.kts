plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

apply(from = rootProject.file("gradle/_sign.gradle"))

android {
    namespace = "arch.cayenne.lib.qyplayer"
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

dependencies {
    //包含所有 .aar 文件
    //api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar")))) ❌，此方式不具有穿透行，命令打包报错
    //api("com.local:QYPlayer:1.0.2@aar") ✅
    //api("QYPlayer-1.0.2@aar") ❌
    //自动检索libs目录下的aar名字并依赖
    fileTree("libs") { include("*.aar") }.forEach { aarFile ->
        val aarName = aarFile.nameWithoutExtension.replace('-',':') // 获取文件名（去除扩展名）
        //println("------->com.local:$aarName@${aarFile.extension}")
        api("com.local:$aarName@${aarFile.extension}")
    }
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}