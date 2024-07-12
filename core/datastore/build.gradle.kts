plugins {
  id("diceroll.android.library")
}

android {
  namespace = "com.appcoins.diceroll.sdk.core.datastore"
}

dependencies {
  implementation(libs.androidx.datastore.preferences)
}