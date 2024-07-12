dependencyResolutionManagement {
    repositories {
        google()
        gradlePluginPortal() // so that external plugins can be resolved in dependencies section
        mavenCentral()
        mavenLocal()
        maven {
            name = "Sonatype-Snapshots"
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
        }
    }
    // Sharing the root project version catalog
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "convention-plugins"
