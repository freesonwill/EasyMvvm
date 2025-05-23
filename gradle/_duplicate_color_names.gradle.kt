tasks.register("checkDuplicateColorNames"){
    doLast {
        // 存储颜色名到模块列表的映射
        def colorMap = [:]

        println("开始检查各模块中的重复颜色名称...")

        project.rootProject.allprojects { project ->
            def resDir = new File(project.projectDir, "src/main/res")
            if (resDir.exists()) {
                def colorFiles = fileTree(dir: resDir, include: '**/color*.xml')

                colorFiles.each { file ->
                    try {
                        def xml = new XmlParser().parse(file)
                        xml.children().each { node ->
                            def name = node.attribute("name")
                            if (name) {
                                if (!colorMap.containsKey(name)) {
                                    colorMap[name] = [] as Set
                                }
                                colorMap[name].add(project.name)
                            }
                        }
                    } catch (Exception e) {
                        println("解析文件 ${file.path} 时出错: ${e.message}")
                    }
                }
            }
        }

        // 过滤出有重复的颜色名称
        def duplicates = colorMap.findAll { it.value.size() > 1 }

        if (duplicates.isEmpty()) {
            println("检查完成，未发现重复的颜色名称。")
        } else {
            println("\n发现以下重复的颜色名称:")
            println("=" * 50)

            duplicates.each { entry ->
                def colorName = entry.key
                        def modules = entry.value.join(", ")
                println("颜色名称: '$colorName'")
                println("存在于模块: $modules")
                println("-" * 50)
            }

            println("\n总共发现 ${duplicates.size()} 个重复的颜色名称。")
        }

        // 你也可以将这些信息写入文件
//        def outputFile = new File(project.buildDir, "duplicate_color_names.txt")
//        outputFile.parentFile.mkdirs()
//
//        outputFile.withWriter { writer ->
//            if (duplicates.isEmpty()) {
//                writer.writeLine("未发现重复的颜色名称")
//            } else {
//                writer.writeLine("重复的颜色名称报告:")
//                duplicates.each { entry ->
//                    writer.writeLine("'${entry.key}' - 模块: ${entry.value.join(", ")}")
//                }
//                writer.writeLine("\n总计: ${duplicates.size()} 个重复项")
//            }
//        }
//
//        println("\n详细报告已生成至: ${outputFile.absolutePath}")
    }
}