pluginManagement {
    repositories {
        apply("maven.gradle.kts")

        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        apply("maven.gradle.kts")

        google()
        mavenCentral()
    }
}

rootProject.name = "Itinerary-android"
include(":app")

include(":core")
include(":core:common")
include(":core:network")
include(":core:database")
include(":core:model")
include(":core:ui")

include(":feature")
include(":feature:login")
include(":feature:splash")
include(":feature:home")
include(":feature:im")
include(":feature:mine")

// 内部组件通信模块 (必要模块)
include(":tuicore")
project(":tuicore").projectDir = file("tuicore")

// IM 组件公共模块（必要模块）
include(":timcommon")
project(":timcommon").projectDir = file("/timcommon")

// 聊天功能模块 (基础功能模块)
include(":tuichat")
project(":tuichat").projectDir = file("/tuichat")

// 会话功能模块 (基础功能模块)
include(":tuiconversation")
project(":tuiconversation").projectDir = file("/tuiconversation")

// 关系链功能模块 (基础功能模块)
include(":tuicontact")
project(":tuicontact").projectDir = file("/tuicontact")

// 群组功能模块
include(":tuigroup")
project(":tuigroup").projectDir = file("/tuigroup")