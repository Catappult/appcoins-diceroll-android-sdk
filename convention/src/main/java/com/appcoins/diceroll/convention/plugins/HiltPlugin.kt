package com.appcoins.diceroll.convention.plugins

import com.appcoins.diceroll.convention.extensions.implementation
import com.appcoins.diceroll.convention.extensions.ksp
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("dagger.hilt.android.plugin")
        // KAPT must go last to avoid build warnings.
        // See: https://stackoverflow.com/questions/70550883/warning-the-following-options-were-not-recognized-by-any-processor-dagger-f
        apply("com.google.devtools.ksp")
      }
      dependencies {
        implementation("hilt.android")
        ksp("hilt.compiler")
        implementation("androidx-compose-hilt-navigation")
      }
    }
  }
}