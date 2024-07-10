pluginManagement {
  repositories {
    google()
    mavenCentral()
    mavenLocal()
    gradlePluginPortal()
    flatDir {
      dirs("libs")
    }
    maven {
      name = "Sonatype-Snapshots"
      url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
  }
}

buildCache {
  local {
    removeUnusedEntriesAfterDays = 30
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
  repositories {
    google()
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://jitpack.io") }
    maven {
      name = "Sonatype-Snapshots"
      url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }  }
}

val (projects, modules) = rootDir.projectsAndModules()
println("Projects:\n\t- ${projects.sortedBy { it }.joinToString("\n\t- ") { it }}")
println("Modules:\n\t- ${modules.sortedBy { it }.joinToString("\n\t- ") { it }}")

for (project in projects) includeBuild(project)
for (module in modules) include(module)


fun File.projectsAndModules(): Pair<Set<String>, Set<String>> {
  val blacklist = setOf(
    ".git",
    ".gradle",
    ".idea",
    "buildSrc",
    "config",
    "build",
    "src"
  )

  fun File.childrenDirectories() = listFiles { _, name -> name !in blacklist }
    ?.filter { it.isDirectory }
    .orEmpty()

  fun File.isProject() = File(this, "settings.gradle.kts").exists() ||
      File(this, "settings.gradle").exists()

  fun File.isModule() = !isProject() &&
      (File(this, "build.gradle.kts").exists() || File(this, "build.gradle.kts").exists())


  val modules = mutableSetOf<String>()
  val projects = mutableSetOf<String>()

  fun File.find(name: String? = null, includeModules: Boolean = true): List<File> {
    return childrenDirectories().flatMap {
      val newName = (name ?: "") + it.name
      when {
        it.isProject() -> {
          projects += newName
          it.find("$newName:", includeModules = false)
        }
        it.isModule() && includeModules -> {
          modules += ":$newName"
          it.find("$newName:")
        }
        else -> it.find("$newName:")
      }
    }
  }

  find()

  // we need to replace here since some Projects have a Module as a parent folder
  val formattedProjects = projects.map { it.replace(":", "/") }.toSet()
  return Pair(formattedProjects, modules)
}
