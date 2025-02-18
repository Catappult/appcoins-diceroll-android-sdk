import com.appcoins.diceroll.sdk.convention.extensions.projectImplementation

plugins {
  id("diceroll.android.library")
}

android {
  namespace = "com.appcoins.diceroll.sdk.payments.data"
}

dependencies {
  implementation(project(":core:ui:notifications"))
  implementation(project(":core:network"))
  projectImplementation(":feature:roll-game:data")
  projectImplementation(":core:utils")
  projectImplementation(":core:ui:design")
  implementation(libs.catappult.android.appcoins.billing)
}
