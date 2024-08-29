package com.appcoins.diceroll.sdk.core.utils

val diceRollPackage =
    if (BuildConfig.DEBUG) "com.appcoins.diceroll.sdk.dev" else "com.appcoins.diceroll.sdk"

// TODO Update the correct host
val serverToServerCheckUrl =
    if (BuildConfig.DEBUG) "https://caa4-184-82-148-163.ngrok-free.app/"
    else "https://caa4-184-82-148-163.ngrok-free.app/"
