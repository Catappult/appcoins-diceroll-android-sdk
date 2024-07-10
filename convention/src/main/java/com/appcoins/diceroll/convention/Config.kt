package com.appcoins.diceroll.convention

import org.gradle.api.JavaVersion

object Config {
  val android = AndroidConfig(
    minSdk = 26,
    targetSdk = 34,
    compileSdkVersion = 34,
    ndkVersion = "21.3.6528147"
  )
  val jvm = JvmConfig(
    javaVersion = JavaVersion.VERSION_17,
    kotlinJvm = JavaVersion.VERSION_17.toString(),
    freeCompilerArgs = listOf(
      "-opt-in=kotlin.RequiresOptIn",
      "-opt-in=kotlin.Experimental",
      "-Xsam-conversions=class"
    )
  )

  data class AndroidConfig(
    val minSdk: Int,
    val targetSdk: Int,
    val compileSdkVersion: Int,
    val ndkVersion: String,
  )

  data class JvmConfig(
    val javaVersion: JavaVersion,
    val kotlinJvm: String,
    val freeCompilerArgs: List<String>
  )

  const val versionFlavorDimension = "version"
  const val googlePlayBillingVersion = "googlePlay"
  const val appcoinsBillingVersion = "appcoins"
}