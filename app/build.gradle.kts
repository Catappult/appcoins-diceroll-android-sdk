import com.appcoins.diceroll.convention.extensions.projectImplementation

plugins {
  id("diceroll.android.app")
}

android {
  namespace = "com.appcoins.diceroll"
  defaultConfig {
    applicationId = "com.appcoins.diceroll"
    versionCode = 16
    versionName = "0.4.9"
    multiDexEnabled = true
  }
}

dependencies {
  implementation(libs.catappult.android.appcoins.billing)

  projectImplementation(":core:ui:design")
  projectImplementation(":core:ui:widgets")
  projectImplementation(":core:utils")
  projectImplementation(":core:navigation")
  projectImplementation(":feature:settings:data")
  projectImplementation(":feature:settings:ui")
  projectImplementation(":feature:stats:ui")
  projectImplementation(":feature:roll-game:ui")
  projectImplementation(":feature:payments:ui")
  projectImplementation(":payments:appcoins-sdk")
  implementation(libs.androidx.splashscreen)
  implementation(libs.bundles.androidx.compose)
  implementation(libs.bundles.androidx.compose.accompanist)
}