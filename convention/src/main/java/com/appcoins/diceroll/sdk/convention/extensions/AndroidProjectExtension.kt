package com.appcoins.diceroll.sdk.convention.extensions

import com.android.build.api.dsl.CommonExtension
import com.appcoins.diceroll.sdk.convention.Config
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

internal fun Project.configureAndroidAndKotlin(extension: CommonExtension<*, *, *, *, *>) {
  with(extension) {
    compileSdk = Config.android.compileSdkVersion
    defaultConfig {
      minSdk = Config.android.minSdk
      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
      sourceCompatibility = Config.jvm.javaVersion
      targetCompatibility = Config.jvm.javaVersion
      isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
      jvmTarget = Config.jvm.kotlinJvm
      freeCompilerArgs = freeCompilerArgs + Config.jvm.freeCompilerArgs
    }

    packaging {
      resources.excludes += "META-INF/NOTICE.md"
      resources.excludes += "META-INF/LICENSE.md"
      resources.excludes += "META-INF/LICENSE-notice.md"
    }
  }

  dependencies.apply {
    add("coreLibraryDesugaring", findLibrary("android.desugar"))
  }
}


private fun CommonExtension<*, *, *, *, *>.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
  (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}