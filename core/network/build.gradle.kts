import com.appcoins.diceroll.sdk.convention.extensions.projectImplementation

plugins {
  id("diceroll.android.library")
}

android {
  namespace = "com.appcoins.diceroll.sdk.core.network"
}

dependencies {
  projectImplementation(":core:utils")
  implementation(libs.bundles.network)
}