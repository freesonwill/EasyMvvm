pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        jcenter()
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven(url = "https://jitpack.io")
        maven(url = "https://repo1.maven.org/maven2/")
        maven(url = "https://developer.huawei.com/repo/")
        maven(url = "https://maven.aliyun.com/repository/public/")
        //阿里云jcenter仓库
        maven(url = "https://maven.aliyun.com/repository/jcenter")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

rootProject.name = "wls-android"
include(":app")
include(":lib_base")
include(":lib_common")
include(":lib_database")
include(":module_login")
include(":module_home")
include(":module_setting")
include(":module_videoplayer")
include(":lib_socket")
include(":module_live")
