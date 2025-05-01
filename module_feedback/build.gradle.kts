plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

apply(from = rootProject.file("gradle/flavor.gradle"))
apply(from = rootProject.file("gradle/_koin.gradle"))
android {
    namespace = "com.walisport.module.feedback"
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
    implementation(project(":lib_res"))
    implementation(project(":lib_skin"))
    implementation(project(":lib_common"))
    implementation(project(":lib_database"))
    implementation(project(":lib_socket"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.flexbox)

}