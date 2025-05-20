import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.proto
@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("com.google.protobuf") version "0.9.4"
    id("kotlin-kapt")
}
apply(from = rootProject.file("gradle/_sign.gradle"))

android {
    namespace = "arch.cayenne.lib.chatwebsocket"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        ndk {
            // 只接入 armeabi-v7a 和 x86 架构
            //abiFilters 'armeabi-v7a', 'x86', 'arm64-v8a', 'x86_64'
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a")) // 仅支持 arm 版本
        }
        externalNativeBuild {
            cmake {
                //cppFlags "-Isrc/main/cpp"
                cppFlags += listOf("-std=c++11")
            }
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
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            // version '3.18.1'
        }
    }
    //protobuf設定
    sourceSets {
        getByName("main") {
            proto {
                srcDir("src/main/protos")
            }
        }
    }
    packaging {
        resources {
            excludes += "**/*.proto"
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.30.1"
    }
    generateProtoTasks {
        all().configureEach {
            plugins {
                id("java") {
                    option("lite")
                }

            }
        }
    }
}

dependencies {

    implementation(project(":lib_base"))
    implementation(project(":lib_websocket"))
    implementation(libs.gson)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    api(libs.okhttps)
    api(libs.protobuf)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}