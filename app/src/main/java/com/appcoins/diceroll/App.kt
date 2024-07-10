package com.appcoins.diceroll

import android.app.Application
import com.appcoins.diceroll.payments.appcoins_sdk.SdkManagerImpl
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        SdkManagerImpl.setupSdkConnection(this)
    }

}