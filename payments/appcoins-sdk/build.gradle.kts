import com.appcoins.diceroll.sdk.convention.extensions.projectImplementation

plugins {
  id("diceroll.android.library")
}

android {
  namespace = "com.appcoins.diceroll.sdk.payments.appcoins_sdk"
  buildTypes {
    debug {
      buildConfigField(
        "String",
        "CATAPPULT_PUBLIC_KEY",
        project.property("DICEROLL_SDK_CATAPPULT_PUBLIC_KEY_DEV").toString()
      )

    }
    release {
      buildConfigField(
        "String",
        "CATAPPULT_PUBLIC_KEY",
        project.property("DICEROLL_SDK_CATAPPULT_PUBLIC_KEY").toString()
      )
    }
  }
  buildFeatures {
    buildConfig = true
  }
}

dependencies {
  projectImplementation(":core:ui:notifications")
  projectImplementation(":core:network")
  projectImplementation(":core:utils")
  projectImplementation(":feature:roll-game:data")
  projectImplementation(":feature:settings:data")
  implementation(libs.catappult.android.appcoins.billing)
  implementation(libs.bundles.network)
}