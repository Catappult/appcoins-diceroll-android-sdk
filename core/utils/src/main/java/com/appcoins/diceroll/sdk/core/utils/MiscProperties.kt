package com.appcoins.diceroll.sdk.core.utils

val diceRollPackage =
    if (BuildConfig.DEBUG) "com.appcoins.diceroll.sdk.dev" else "com.appcoins.diceroll.sdk"

const val serverToServerCheckUrl = "https://sdk.diceroll.catappult.io/"

const val rtdnWebSocketUrl = "wss://sdk.diceroll.catappult.io/ws/"
