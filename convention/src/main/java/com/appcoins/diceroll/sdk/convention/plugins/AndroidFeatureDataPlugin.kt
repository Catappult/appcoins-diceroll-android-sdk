package com.appcoins.diceroll.sdk.convention.plugins

import com.appcoins.diceroll.sdk.convention.extensions.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureDataPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      pluginManager.apply {
        apply<AndroidLibraryPlugin>()
      }

      dependencies {
        implementation("network-retrofit")
        implementation("network-retrofit-converter-gson")
        implementation("network-okhttp")
        implementation("network-okhttp-loginterceptor")
        implementation("google-gson")

        implementation("kotlin-coroutines")
        implementation("kotlin-coroutines-test")
        implementation("test-mockk")
        implementation("test-junit")
      }
    }
  }
}