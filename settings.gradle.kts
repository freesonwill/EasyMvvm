pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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
include(":lib_websocket")
include(":lib_skin")
include(":lib_res")
include(":lib_ijkplayer")
include(":module_login")
include(":module_home")
include(":module_setting")
include(":module_live")
include(":module_bet")
include(":module_search")
include(":module_handicap")
include(":plugin_koin")
include(":module_feedback")
include(":module_message")
include(":lib_qyplayer")
