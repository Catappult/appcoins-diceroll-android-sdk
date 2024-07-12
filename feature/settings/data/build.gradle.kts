import com.appcoins.diceroll.sdk.convention.extensions.projectImplementation

plugins {
  id("diceroll.android.feature.data")
}

android {
  namespace = "com.appcoins.diceroll.sdk.feature.settings.data"
}

dependencies {
  projectImplementation(":core:utils")
  projectImplementation(":core:datastore")
  implementation(libs.androidx.datastore.preferences)
}