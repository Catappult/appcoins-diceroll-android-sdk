package com.appcoins.diceroll.payments.appcoins_sdk

import android.content.Intent

data class SdkResult(val requestCode: Int, val resultCode: Int, val data: Intent?)
