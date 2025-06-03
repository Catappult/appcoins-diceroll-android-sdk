package com.appcoins.diceroll.sdk.payments.billing

/**
 * Manages the AppCoins SDK integration for in-app billing.
 *
 * This class initializes the AppCoins billing client, sets up
 * listeners for billing events, and provides methods to interact
 * with the billing service.
 *
 * It serves as a wrapper around the AppCoins SDK to handle all the
 * necessary setup and provide callbacks to the app for billing events
 * in order to simplify the call for it.
 *
 */
interface SdkManager {

    /**
     TODO: Define here the necessary methods and parameters to accomplish the same features in
        Aptoide Billing as they are in the Google Billing in the package:
            com.appcoins.diceroll.sdk.payments.billing
        Steps:
            1 - Follow the documentation in https://docs.catappult.io/docs/android-billing-sdk to integrate Aptoide Billing SDK
            2 - Set the Public Key in your .mavenLocal file for the following fields:
                DICEROLL_SDK_CATAPPULT_PUBLIC_KEY_DEV
                DICEROLL_SDK_CATAPPULT_PUBLIC_KEY
            3 - Once setup and project is build at least once, confirm the access to the Public Key in this file using:
                private val BASE_64_ENCODED_PUBLIC_KEY = BuildConfig.CATAPPULT_PUBLIC_KEY
            4 - Initiate the integration for the Aptoide Billing and confirm the BuildVariant is set to appcoinsDebug or appcoinsRelease
            5 - Add the Aptoide billing Library in the TODO_APTOIDE_SDK_DEPENDENCY comment section
        In the end the Aptoide Billing is supposed to:
            1 - Show correctly the available items to Buy.
            2 - Allow to Buy the item available
            3 - Show the price for the Attempts in the Main Screen
            4 - Allow to buy the Golden Dice
            5 - Verify if there are new versions available in the Settings
     */

    companion object {
        const val LOG_TAG = "SdkManager"
    }
}
