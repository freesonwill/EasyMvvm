import com.google.gson.Gson
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

apply(from = rootProject.file("gradle/flavor.gradle"))
apply(from = rootProject.file("gradle/_sign.gradle"))

val prop = Properties().apply {
    load(project.rootProject.file("local.properties").inputStream())
}

android {
    namespace = "com.walisport.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.walisport.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val uid = prop.getProperty("user.uid")?.also { buildConfigField("int", "uid", it) }
        val token = prop.getProperty("user.token")?.also { buildConfigField("String", "token", it.let { "\"$it\"" }) }
        val name = prop.getProperty("user.name")?.also {  buildConfigField("String", "name", it.let { "\"$it\"" })}
        providers.gradleProperty("users").orNull?.let { users->
            data class UserConfig(val name: String, val uid: String, val token: String)
            val userList: List<UserConfig> = Gson().fromJson(users, Array<UserConfig>::class.java).toList()
            val escapedJson = users.replace("\\", "\\\\").replace("\"", "\\\"")
            buildConfigField("String", "users", escapedJson.let { "\"$it\"" })
            //user.name优先user.uid
            (userList.find { it.name == name } ?: userList.find { it.uid == uid })?.let { user ->
                if(name.isNullOrEmpty()) buildConfigField("String", "name", user.name.let { "\"$it\"" })
                buildConfigField("int", "uid", user.uid)
                buildConfigField("String", "token", user.token.let { "\"$it\"" })
            }?: error("both user.name:${name} and user.uid:${uid} not defined in properties")
        }

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
    packaging {
        resources {
            excludes += "**/*.proto"
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation(project(":lib_common"))
    implementation(project(":lib_websocket"))
    implementation(project(":lib_http"))
    implementation(project(":lib_base"))
    implementation(project(":module_home"))
    implementation(project(":module_bet"))
    implementation(project(":module_betslip"))
    implementation(project(":module_login"))
    implementation(project(":module_setting"))
    implementation(project(":module_live"))
    implementation(project(":module_handicap"))
    implementation(project(":module_search"))
    implementation(project(":module_feedback"))
    implementation(project(":module_message"))
    implementation(project(":module_topup"))
    implementation(project(":module_account"))
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
    if(prop.getProperty("PERF_BLOCK_CANARY","false").toBoolean()) {
        debugImplementation(project(":external:blockcanary"))
    }
    debugImplementation(project(":external:lib_perf"))
    implementation(project(":external:lib_test"))
}