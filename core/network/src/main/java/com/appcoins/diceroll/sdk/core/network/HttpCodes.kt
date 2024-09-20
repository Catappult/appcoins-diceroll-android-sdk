package com.appcoins.diceroll.sdk.core.network

object HttpCodes {

    private const val INFO_START_CODE = 100
    private const val INFO_END_CODE = 199

    private const val SUCCESS_START_CODE = 200
    private const val SUCCESS_END_CODE = 299

    private const val REDIRECT_START_CODE = 300
    private const val REDIRECT_END_CODE = 399

    private const val CLIENT_ERROR_START_CODE = 400
    private const val CLIENT_ERROR_END_CODE = 499

    private const val SERVER_ERROR_START_CODE = 500
    private const val SERVER_ERROR_END_CODE = 599

    fun Int.isInfo(): Boolean = this in INFO_START_CODE..INFO_END_CODE
    fun Int.isSuccess(): Boolean = this in SUCCESS_START_CODE..SUCCESS_END_CODE
    fun Int.isRedirect(): Boolean = this in REDIRECT_START_CODE..REDIRECT_END_CODE
    fun Int.isClientError(): Boolean = this in CLIENT_ERROR_START_CODE..CLIENT_ERROR_END_CODE
    fun Int.isServerError(): Boolean = this in SERVER_ERROR_START_CODE..SERVER_ERROR_END_CODE
}