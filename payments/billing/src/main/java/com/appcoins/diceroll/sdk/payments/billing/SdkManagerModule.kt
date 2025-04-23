package com.appcoins.diceroll.sdk.payments.billing

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SdkManagerModule {

    @Provides
    @Singleton
    fun provideSdkManager(sdkManagerImpl: SdkManagerImpl): SdkManager =
        sdkManagerImpl
}
