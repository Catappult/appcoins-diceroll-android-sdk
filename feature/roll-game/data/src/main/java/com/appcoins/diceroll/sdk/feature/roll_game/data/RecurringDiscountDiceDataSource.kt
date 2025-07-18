package com.appcoins.diceroll.sdk.feature.roll_game.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecurringDiscountDiceDataSource @Inject constructor(
    private val preferences: DataStore<Preferences>,
) {

    /**
     * Updates the status of the Recurring Discount Dice subscription.
     */
    suspend fun saveRecurringDiscountDiceStatus(active: Boolean) {
        withContext(Dispatchers.IO) {
            preferences.edit { prefs ->
                prefs[RECURRING_DISCOUNT_DICE_ACTIVE] = active
            }
        }
    }

    /**
     * Stream of Recurring Discount Dice status [Boolean].
     */
    fun getRecurringDiscountDiceStatus(): Flow<Boolean> {
        return preferences.data.map { prefs ->
            prefs[RECURRING_DISCOUNT_DICE_ACTIVE] ?: false
        }.distinctUntilChanged()
    }

    companion object {
        val RECURRING_DISCOUNT_DICE_ACTIVE = booleanPreferencesKey("recurring_discount_dice_active")
    }
}
