pluginManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
        
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
        maven { url = uri("https://jitpack.io") }

        google()
        mavenCentral()
    }
}

rootProject.name = "Itinerary-android"
include(":app")

include(":localAar")

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