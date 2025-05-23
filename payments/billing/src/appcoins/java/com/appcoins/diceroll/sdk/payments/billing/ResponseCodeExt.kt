package com.appcoins.diceroll.sdk.payments.billing

import com.appcoins.sdk.billing.ResponseCode

fun Int.toResponseCode(): ResponseCode {
  for (code in ResponseCode.values()) {
    if (code.value == this) {
      return code
    }
  }
  throw IllegalArgumentException("Invalid ResponseCode value: $this")
}
