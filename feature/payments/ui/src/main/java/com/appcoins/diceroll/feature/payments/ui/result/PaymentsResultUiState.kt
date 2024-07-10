package com.appcoins.diceroll.feature.payments.ui.result

/**
 * A sealed hierarchy describing the UI result state coming from the integration the user used to pay.
 * This is used to show an information about what happened with the payment.
 */
sealed interface PaymentsResultUiState {

  /**
   * The dialog is opened and the payment integration has not been called yet.
   */
  data object Initialized : PaymentsResultUiState

  /**
   * The dialog is opened and its waiting for the payment to complete.
   */
  data object Loading : PaymentsResultUiState

  /**
   * The dialog is opened, but the user exited the payment dialog.
   */
  data object UserCanceled : PaymentsResultUiState

  /**
   * The dialog is opened, but the payment was not successful.
   */
  data object Failed : PaymentsResultUiState

  /**
   * The dialog is opened and the payment was successful.
   */
  data object Success : PaymentsResultUiState
}
