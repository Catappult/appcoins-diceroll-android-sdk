package com.appcoins.diceroll.sdk

import android.app.Application
import com.appcoins.diceroll.sdk.payments.appcoins_sdk.SdkManagerImpl
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        SdkManagerImpl.setupSdkConnection(this)
    }

}