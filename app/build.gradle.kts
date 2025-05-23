import com.appcoins.diceroll.sdk.convention.extensions.projectImplementation

plugins {
  id("diceroll.android.app")
}

android {
  namespace = "com.appcoins.diceroll.sdk"
  defaultConfig {
    applicationId = "com.appcoins.diceroll.sdk"
    versionCode = 100
    versionName = "0.5.0"
    multiDexEnabled = true
  }
}

dependencies {
  implementation(libs.catappult.android.appcoins.billing)
  projectImplementation(":core:ui:design")
  projectImplementation(":core:ui:widgets")
  projectImplementation(":core:utils")
  projectImplementation(":core:permissions")
  projectImplementation(":core:navigation")
  projectImplementation(":feature:roll-game:data")
  projectImplementation(":feature:settings:data")
  projectImplementation(":feature:settings:ui")
  projectImplementation(":feature:stats:ui")
  projectImplementation(":feature:store:ui")
  projectImplementation(":feature:roll-game:ui")
  projectImplementation(":feature:payments:ui")
  projectImplementation(":payments:billing")
  projectImplementation(":payments:data")
  implementation(libs.androidx.splashscreen)
  implementation(libs.bundles.androidx.compose)
  implementation(libs.bundles.androidx.compose.accompanist)
}
