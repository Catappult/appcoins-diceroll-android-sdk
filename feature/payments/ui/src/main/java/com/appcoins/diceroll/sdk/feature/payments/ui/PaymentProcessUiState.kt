package com.appcoins.diceroll.sdk.feature.payments.ui

/**
 * A sealed hierarchy describing the UI payment availability content state.
 * This is used to control the state of a payment availability.
 */
sealed interface PaymentProcessUiState {
  /**
   * The dialog is opened and loading its content.
   */
  data object Loading : PaymentProcessUiState

  /**
   * The dialog is opened, but there was an Error when loading the content.
   */
  data object Error : PaymentProcessUiState

  /**
   * The dialog is opened, but the payment option is not available.
   */
  data object NotAvailable : PaymentProcessUiState

  /**
   * The dialog is opened and payment is in progress.
   */
  data object StartPayment : PaymentProcessUiState

  /**
   * The dialog is opened and payment is in progress.
   */
  data object PaymentInProgress : PaymentProcessUiState
}
