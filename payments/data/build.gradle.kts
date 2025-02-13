import com.appcoins.diceroll.sdk.convention.extensions.projectImplementation

plugins {
  id("diceroll.android.library")
}

android {
  namespace = "com.appcoins.diceroll.sdk.payments.data"
}

dependencies {
  projectImplementation(":core:utils")
  implementation(libs.catappult.android.appcoins.billing)
}
