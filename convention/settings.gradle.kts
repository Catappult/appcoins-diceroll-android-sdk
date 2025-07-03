dependencyResolutionManagement {
    repositories {
        google()
        gradlePluginPortal() // so that external plugins can be resolved in dependencies section
        mavenCentral()
        mavenLocal()
        maven {
            name = "MavenCentral-Snapshots"
            url = uri("https://central.sonatype.com/repository/maven-snapshots")
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
