package com.appcoins.diceroll.convention.extensions

import org.gradle.api.Project

/**
 * Adds a library dependency to the "implementation", "testImplementation",
 * "androidTestImplementation" or "debugImplementation" configuration.
 *
 * It also adds the dependency to the "kapt" or "ksp" configuration if the library
 * is annotation processed.
 *
 * This is only used within the convention plugins and not in the main Gradle
 * build implementations.
 *
 * @param alias the library alias to add as a dependency as defined in the [libs.versions.toml] file
 * without the "libs." prefix (e.g. "kotlin-coroutines") instead of "libs.kotlin.coroutines")
 */


internal fun Project.implementation(alias: String) {
  dependencies.add("implementation", findLibrary(alias))
}

internal fun Project.testImplementation(alias: String) {
  dependencies.add("testImplementation", findLibrary(alias))
}

internal fun Project.androidTestImplementation(alias: String) {
  dependencies.add("androidTestImplementation", findLibrary(alias))
}

internal fun Project.debugImplementation(alias: String) {
  dependencies.add("debugImplementation", findLibrary(alias))
}

internal fun Project.kapt(alias: String) {
  dependencies.add("kapt", findLibrary(alias))
}

internal fun Project.ksp(alias: String) {
  dependencies.add("ksp", findLibrary(alias))
}
