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
    }
}

rootProject.name = "wls-android"
include(":app")
include(":lib_base")
include(":lib_common")
include(":lib_database")
include(":lib_socket")
include(":module_login")
include(":module_home")
include(":module_setting")
include(":module_live")
include(":module_bet")
