package com.appcoins.diceroll.sdk.core.utils

val diceRollPackage =
    if (BuildConfig.DEBUG) "com.appcoins.diceroll.sdk.dev" else "com.appcoins.diceroll.sdk"

val serverToServerCheckUrl =
    if (BuildConfig.DEBUG) "https://api.dev.catappult.io/product/8.20240810/"
    else "https://api.catappult.io/product/8.20240810/"
