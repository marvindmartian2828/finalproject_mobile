// Plugin management configuration for Gradle
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()   // For non-Android dependencies
        gradlePluginPortal()  // For Gradle plugins like Kotlin, Compose, etc.
    }
}

// Dependency resolution settings for all modules
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)  // Enforces central repo usage
    repositories {
        google()  // Android and Google libraries
        mavenCentral()  // General-purpose open-source libraries
    }
}

rootProject.name = "projDraft_AutoVitals"
include(":app")  // Includes the app module in the build
 