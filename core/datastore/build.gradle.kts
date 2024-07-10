plugins {
  id("diceroll.android.library")
}

android {
  namespace = "com.appcoins.diceroll.core.datastore"
}

dependencies {
  implementation(libs.androidx.datastore.preferences)
}