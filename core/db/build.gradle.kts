import com.appcoins.diceroll.convention.extensions.projectImplementation

plugins {
  id("diceroll.android.library")
  id("diceroll.room")
}

android {
  namespace = "com.appcoins.diceroll.core.db"
}

dependencies {
  projectImplementation(":core:utils")
}