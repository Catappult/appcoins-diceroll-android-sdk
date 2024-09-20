package com.appcoins.diceroll.sdk.core.network.modules

import com.appcoins.diceroll.sdk.core.network.annotations.HttpClient
import com.appcoins.diceroll.sdk.core.network.annotations.PurchaseValidatorRetrofitClient
import com.appcoins.diceroll.sdk.core.network.modules.api.PurchaseValidatorApi
import com.appcoins.diceroll.sdk.core.utils.serverToServerCheckUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class PurchaseValidatorApiModule {

    @Singleton
    @Provides
    @PurchaseValidatorRetrofitClient
    fun providePurchaseValidatorRetrofit(@HttpClient client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(serverToServerCheckUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun providePurchaseValidatorApi(@PurchaseValidatorRetrofitClient retrofit: Retrofit): PurchaseValidatorApi {
        return retrofit.create(PurchaseValidatorApi::class.java)
    }
}
