package com.appcoins.diceroll.sdk

import android.app.Application
import com.appcoins.diceroll.sdk.payments.appcoins_sdk.SdkManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var sdkManager: SdkManager

    override fun onCreate() {
        super.onCreate()

        sdkManager.setupSdkConnection(this)
    }

}