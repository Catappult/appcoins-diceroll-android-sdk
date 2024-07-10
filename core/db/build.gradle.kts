import com.appcoins.diceroll.sdk.convention.extensions.projectImplementation

plugins {
  id("diceroll.android.library")
  id("diceroll.room")
}

android {
  namespace = "com.appcoins.diceroll.sdk.core.db"
}

dependencies {
  projectImplementation(":core:utils")
}