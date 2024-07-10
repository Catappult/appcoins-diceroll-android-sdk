import com.appcoins.diceroll.convention.extensions.projectImplementation

plugins {
  id("diceroll.android.feature.data")
}

android {
  namespace = "com.appcoins.diceroll.feature.stats.data"
}

dependencies {
  projectImplementation(":core:utils")
  projectImplementation(":core:db")
  implementation(libs.androidx.datastore.preferences)
}