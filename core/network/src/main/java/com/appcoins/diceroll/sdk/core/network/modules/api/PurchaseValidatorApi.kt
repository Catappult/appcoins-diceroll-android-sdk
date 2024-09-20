package com.appcoins.diceroll.sdk.core.network.modules.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PurchaseValidatorApi {

    @GET("validate/{packageName}/{productId}/{token}")
    suspend fun getPurchaseValidationState(
        @Path("packageName") packageName: String,
        @Path("productId") sku: String,
        @Path("token") purchaseToken: String
    ): Response<String>
}
