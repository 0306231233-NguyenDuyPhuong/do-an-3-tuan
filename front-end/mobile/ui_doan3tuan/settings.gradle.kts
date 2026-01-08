pluginManagement {
    repositories {
        google() // Đã bỏ phần content filter để tránh lỗi thiếu dependency
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ui_doan3tuan"
include(":app")