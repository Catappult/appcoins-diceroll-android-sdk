import com.appcoins.diceroll.sdk.convention.extensions.projectImplementation

plugins {
    id("diceroll.android.feature.ui")
}

android {
    namespace = "com.appcoins.diceroll.sdk.feature.store.ui"
}

dependencies {
    projectImplementation(":feature:settings:data")
    projectImplementation(":feature:stats:data")
    projectImplementation(":feature:roll-game:data")
    projectImplementation(":feature:payments:ui")
    projectImplementation(":payments:appcoins-sdk")
    projectImplementation(":payments:data")
    projectImplementation(":core:ui:design")
    projectImplementation(":core:utils")
    projectImplementation(":core:navigation")
    implementation(libs.bundles.coil)
    implementation(libs.catappult.android.appcoins.billing)
}
