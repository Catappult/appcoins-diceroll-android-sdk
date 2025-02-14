import com.appcoins.diceroll.sdk.convention.extensions.projectImplementation

plugins {
  id("diceroll.android.app")
}

android {
  namespace = "com.appcoins.diceroll.sdk"
  defaultConfig {
    applicationId = "com.appcoins.diceroll.sdk"
    versionCode = 24
    versionName = "0.4.17"
    multiDexEnabled = true
  }
}

dependencies {
  implementation(libs.catappult.android.appcoins.billing)
    implementation(project(":payments:data"))
    implementation(project(":feature:roll-game:data"))

    projectImplementation(":core:ui:design")
  projectImplementation(":core:ui:widgets")
  projectImplementation(":core:utils")
  projectImplementation(":core:navigation")
  projectImplementation(":feature:settings:data")
  projectImplementation(":feature:settings:ui")
  projectImplementation(":feature:stats:ui")
  projectImplementation(":feature:store:ui")
  projectImplementation(":feature:roll-game:ui")
  projectImplementation(":feature:payments:ui")
  projectImplementation(":payments:appcoins-sdk")
  implementation(libs.androidx.splashscreen)
  implementation(libs.bundles.androidx.compose)
  implementation(libs.bundles.androidx.compose.accompanist)
}
