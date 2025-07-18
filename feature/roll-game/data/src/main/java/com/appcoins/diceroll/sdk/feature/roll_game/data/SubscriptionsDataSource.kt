package com.appcoins.diceroll.sdk.feature.roll_game.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription.DEFAULT
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.SubscriptionPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SubscriptionsDataSource @Inject constructor(
    private val preferences: DataStore<Preferences>,
) {

    /**
     * Save Selected Subscription.
     */
    suspend fun saveSelectedSubscription(subscription: Subscription) {
        withContext(Dispatchers.IO) {
            preferences.edit { prefs ->
                prefs[SELECTED_SUBSCRIPTION] = subscription.ordinal
            }
        }
    }

    /**
     * Process Expired Subscription
     */
    suspend fun processExpiredSubscription(subscription: Subscription) {
        withContext(Dispatchers.IO) {
            val selectedSubscription =
                preferences.data.map { prefs -> prefs[SELECTED_SUBSCRIPTION] ?: 0 }.first()
            if (selectedSubscription == subscription.ordinal) {
                saveSelectedSubscription(DEFAULT)
            }
        }
    }

    /**
     * Stream of Subscriptions Preferences.
     */
    fun getSubscriptionPreferences(): Flow<SubscriptionPrefs> {
        return preferences.data.map {
            val mutableListAvailableSubscriptions = mutableListOf<Subscription>()

            if (it[GOLDEN_DICE_ACTIVE] == true) {
                mutableListAvailableSubscriptions.add(Subscription.GOLDEN_DICE)
            }
            if (it[TRIAL_DICE_ACTIVE] == true) {
                mutableListAvailableSubscriptions.add(Subscription.TRIAL_DICE)
            }
            if (it[PREPAID_DICE_ACTIVE] == true) {
                mutableListAvailableSubscriptions.add(Subscription.PREPAID_DICE)
            }
            if (it[SINGLE_DISCOUNT_DICE_ACTIVE] == true) {
                mutableListAvailableSubscriptions.add(Subscription.SINGLE_DISCOUNT_DICE)
            }
            if (it[RECURRING_DISCOUNT_DICE_ACTIVE] == true) {
                mutableListAvailableSubscriptions.add(Subscription.RECURRING_DISCOUNT_DICE)
            }

            val selectedSubscription = Subscription.entries[it[SELECTED_SUBSCRIPTION] ?: 0]

            SubscriptionPrefs(mutableListAvailableSubscriptions, selectedSubscription)
        }.distinctUntilChanged()
    }

    companion object {
        val GOLDEN_DICE_ACTIVE = booleanPreferencesKey("golden_dice_active")
        val TRIAL_DICE_ACTIVE = booleanPreferencesKey("trial_dice_active")
        val PREPAID_DICE_ACTIVE = booleanPreferencesKey("prepaid_dice_active")
        val SINGLE_DISCOUNT_DICE_ACTIVE = booleanPreferencesKey("single_discount_dice_active")
        val RECURRING_DISCOUNT_DICE_ACTIVE = booleanPreferencesKey("recurring_discount_dice_active")
        val SELECTED_SUBSCRIPTION = intPreferencesKey("selected_subscription")
    }
}
