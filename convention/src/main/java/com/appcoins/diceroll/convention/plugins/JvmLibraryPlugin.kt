package com.appcoins.diceroll.convention.plugins

import com.appcoins.diceroll.convention.Config
import com.appcoins.diceroll.convention.extensions.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class JvmLibraryPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      pluginManager.apply {
        apply("kotlin")
        apply("org.jetbrains.kotlin.jvm")
      }
      extensions.configure(JavaPluginExtension::class.java) {
        sourceCompatibility = Config.jvm.javaVersion
        targetCompatibility = Config.jvm.javaVersion
      }

      tasks.withType(KotlinCompile::class.java) {
        kotlinOptions {
          jvmTarget = Config.jvm.kotlinJvm
          freeCompilerArgs = freeCompilerArgs
        }
      }

      dependencies.apply {
        implementation("kotlin.stdlib")
      }
    }
  }
}