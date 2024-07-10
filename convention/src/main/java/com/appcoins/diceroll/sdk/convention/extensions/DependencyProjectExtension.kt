package com.appcoins.diceroll.sdk.convention.extensions

import org.gradle.api.Project


/**
 * Adds a project (or a module) dependency to the "implementation", "androidTestImplementation"
 * or "testImplementation" configuration.
 *
 * @param path the path to the project to add as a dependency
 */

fun Project.projectImplementation(path: String) {
  dependencies.add("implementation", project(path))
}

fun Project.projectAndroidTestImplementation(path: String) {
  dependencies.add("androidTestImplementation", project(path))
}

fun Project.projectTestImplementation(path: String) {
  dependencies.add("testImplementation", project(path))
}
