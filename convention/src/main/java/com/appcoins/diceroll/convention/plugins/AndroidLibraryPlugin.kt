package com.appcoins.diceroll.convention.plugins

import com.android.build.gradle.LibraryExtension
import com.appcoins.diceroll.convention.Config
import com.appcoins.diceroll.convention.extensions.configureAndroidAndKotlin
import com.appcoins.diceroll.convention.extensions.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("com.android.library")
        apply("kotlin-android")
        apply<HiltPlugin>()
      }

      extensions.configure<LibraryExtension> {
        configureAndroidAndKotlin(this)
        defaultConfig.targetSdk = Config.android.targetSdk

        flavorDimensions.add(Config.versionFlavorDimension)
        productFlavors {
          create(Config.googlePlayBillingVersion) {
            dimension = Config.versionFlavorDimension
          }
          create(Config.appcoinsBillingVersion) {
            dimension = Config.versionFlavorDimension
          }
        }
      }

      dependencies {
        implementation("kotlin.stdlib")
      }
    }
  }
}