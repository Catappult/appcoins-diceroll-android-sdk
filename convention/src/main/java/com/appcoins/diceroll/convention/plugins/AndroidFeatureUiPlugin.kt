package com.appcoins.diceroll.convention.plugins

import com.android.build.gradle.LibraryExtension
import com.appcoins.diceroll.convention.extensions.configureAndroidCompose
import com.appcoins.diceroll.convention.extensions.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidFeatureUiPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      pluginManager.apply {
        apply<AndroidLibraryPlugin>()
        val extension = extensions.getByType<LibraryExtension>()
        configureAndroidCompose(extension)
      }

      dependencies {
        implementation("androidx-compose-lifecycle-viewModel")
        implementation("androidx-compose-lifecycle-runtime")
        implementation("androidx-compose-ui-util")
        implementation("androidx-compose-ui-ui")
        implementation("androidx-compose-ui-tooling-preview")
        implementation("androidx-compose-ui-tooling")
        implementation("androidx-compose-ui-test")
        implementation("androidx-compose-foundation-layout")
        implementation("androidx-compose-activity")
        implementation("androidx-compose-runtime-tracing")
        implementation("androidx-compose-constraintlayout")
        implementation("coil-kt")
        implementation("coil-kt-compose")
        implementation("coil-kt-svg")
      }
    }
  }
}