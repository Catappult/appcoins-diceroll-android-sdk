package com.appcoins.diceroll.sdk.convention.plugins

import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.appcoins.diceroll.sdk.convention.Config
import com.appcoins.diceroll.sdk.convention.extensions.configureAndroidAndKotlin
import com.appcoins.diceroll.sdk.convention.extensions.version
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidAppPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("com.android.application")
        apply("kotlin-android")
        apply("kotlin-parcelize")
        apply<HiltPlugin>()
      }

      extensions.configure<BaseAppModuleExtension> {
        configureAndroidAndKotlin(this)
        ndkVersion = Config.android.ndkVersion
        defaultConfig {
          targetSdk = Config.android.targetSdk
          multiDexEnabled = true
        }

        buildTypes {
          debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".dev"
            versionNameSuffix = ".dev"
          }

          release {
            isDebuggable = true
            isMinifyEnabled = false
            // To publish to the Play Store, we need to set the signing config with a private
            // signing key, but in order to allow anyone who clones the code to run the release
            // variant, we use the debug signing key.
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
          }
        }

        flavorDimensions.add(Config.versionFlavorDimension)
        productFlavors {
          create(Config.googlePlayBillingVersion) {
            dimension = Config.versionFlavorDimension
            applicationIdSuffix = ".gp"
            versionNameSuffix = ".gp"
          }
          create(Config.appcoinsBillingVersion) {
            dimension = Config.versionFlavorDimension
          }
        }

        applicationVariants.all {
          val sep = "_"
          val buildType = buildType.name
          val flavor = flavorName
          val versionName = versionName
          val versionCode = versionCode
          val fileName = "DiceRoll_v$versionName$sep$versionCode$sep$flavor$sep$buildType.apk"
          outputs.all {
            (this as BaseVariantOutputImpl).outputFileName = fileName
          }
        }

        buildFeatures {
          buildConfig = true
          composeOptions {
            kotlinCompilerExtensionVersion = version("androidx-compose-compiler")
          }
          compose = true
        }
      }
    }
  }
}


