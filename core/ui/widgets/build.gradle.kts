import com.appcoins.diceroll.convention.extensions.projectImplementation

plugins {
  id("diceroll.android.library.compose")
}

android {
  namespace = "com.appcoins.diceroll.core.ui.widgets"

  buildFeatures {
    buildConfig = true
  }

  defaultConfig {
    buildConfigField(
      "String",
      "SDK_BILLING_LIBRARY_VERSION",
      "\"${libs.catappult.android.appcoins.billing.get().version}\""
    )
  }
}

dependencies {
  projectImplementation(":core:ui:design")
  implementation(libs.bundles.androidx.compose)
  implementation(libs.lottie)
}