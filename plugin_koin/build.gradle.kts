plugins {
    kotlin("jvm")
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.24-1.0.20")
}

val jdkVersion = JavaVersion.current().majorVersion.toInt()
kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(jdkVersion)) // 使用 JDK 21 进行编译
    }
}
if (jdkVersion >= 21) {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17" // 生成与 JDK 17 兼容的字节码
        }
    }
    tasks.withType<JavaCompile> {
        options.release.set(17) // 强制生成 JDK 17 兼容的字节码
    }
}
