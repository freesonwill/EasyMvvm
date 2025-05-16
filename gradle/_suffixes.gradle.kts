tasks.register("addResourceSuffixes") {
    doLast {
        val projectDir = project.layout.projectDirectory.asFile
        val themes: List<String> = (project.findProperty("Themes") as? String)
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() } // ✅ 忽略空项（防止末尾逗号导致的问题）
            ?: emptyList()
        println("addResourceSuffixes themes: $themes")
        themes.forEach {
            val dir = File(projectDir, "src/main/res-$it")
            if (dir.exists()) {
                processResources(dir, "_$it")
            }
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