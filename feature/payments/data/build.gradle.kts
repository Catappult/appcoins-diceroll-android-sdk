import com.appcoins.diceroll.sdk.convention.extensions.projectImplementation

plugins {
  id("diceroll.android.feature.data")
}

android {
  namespace = "com.appcoins.diceroll.sdk.feature.payments.data"
}

dependencies {
  compileOnly(fileTree(mapOf("dir" to "libs", "include" to "*.aar")))
}
