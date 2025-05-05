
tasks.register("addResourceSuffixes") {
    doLast {
        val projectDir = project.layout.projectDirectory.asFile
        val blackBlueResDir = File(projectDir, "src/main/res-black_blue")
        if (blackBlueResDir.exists()) {
            processResources(blackBlueResDir, "_black_blue")
        }
        val blackRedResDir = File(projectDir, "src/main/res-black_red")
        if (blackRedResDir.exists()) {
            processResources(blackRedResDir, "_black_red")
        }
        val classicResDir = File(projectDir, "src/main/res-classic")
        if (classicResDir.exists()) {
            processResources(classicResDir, "_classic")
        }
        val whiteBlueResDir = File(projectDir, "src/main/res-white_blue")
        if (whiteBlueResDir.exists()) {
            processResources(whiteBlueResDir, "_white_blue")
        }
        val whiteGreenResDir = File(projectDir, "src/main/res-white_green")
        if (whiteGreenResDir.exists()) {
            processResources(whiteGreenResDir, "_white_green")
        }
    }
}

fun processResources(resDir: File, suffix: String) {
    // 处理 drawable 和 mipmap 目录
    listOf("drawable", "mipmap").forEach { dirName ->
        val dir = File(resDir, dirName)
        if (dir.exists()) {
            dir.listFiles()?.forEach { file ->
                if (file.isFile && !file.nameWithoutExtension.endsWith(suffix)) {
                    val newName = "${file.nameWithoutExtension}$suffix.${file.extension}"
                    file.renameTo(File(file.parent, newName))
                }
            }
        }
    }
//_light|_black
    // 处理 color 文件
    val colorDir = File(resDir, "values")
    if (colorDir.exists()) {
        colorDir.listFiles()?.forEach { file ->
            if (file.isFile && (file.name.startsWith("colors") || file.name.contains("color"))) {
                val content = file.readText()
                val modifiedContent = content.replace(
                    Regex("""<color name="([^"]+?)(?<!$suffix)">"""),
                    """<color name="$1$suffix">"""
                )
                file.writeText(modifiedContent)
            }
        }
    }
}

// 更现代的配置方式  由于每次编译都要检查一遍资源文件 暂时注释调 使用 ./gradlew addResourceSuffixes手动调用
//afterEvaluate {
//    tasks.named("preBuild") {
//        dependsOn("addResourceSuffixes")
//    }
//}