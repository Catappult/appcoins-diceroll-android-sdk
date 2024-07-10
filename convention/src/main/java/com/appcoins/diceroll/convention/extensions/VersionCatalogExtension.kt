package com.appcoins.diceroll.convention.extensions

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

private val Project.libs: VersionCatalog
  get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.findLibrary(alias: String): Provider<MinimalExternalModuleDependency> {
  val optionalDependency = libs.findLibrary(alias)
  if (optionalDependency.isEmpty) {
    error("$alias is not a valid dependency, check your version catalog")
  }
  return optionalDependency.get()
}

internal fun Project.version(alias: String): String {
  return libs.findVersion(alias).get().toString()
}