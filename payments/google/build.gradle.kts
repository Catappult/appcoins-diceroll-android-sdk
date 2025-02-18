import com.appcoins.diceroll.sdk.convention.extensions.projectImplementation

plugins {
    id("diceroll.android.library")
}

android {
    namespace = "com.appcoins.diceroll.sdk.payments.google"
}

dependencies {
    projectImplementation(":core:network")
    projectImplementation(":core:ui:notifications")
    projectImplementation(":core:utils")
    projectImplementation(":feature:roll-game:data")
    projectImplementation(":feature:settings:data")
    projectImplementation(":payments:data")
    implementation(libs.google.billing)
    implementation(libs.bundles.network)
}
