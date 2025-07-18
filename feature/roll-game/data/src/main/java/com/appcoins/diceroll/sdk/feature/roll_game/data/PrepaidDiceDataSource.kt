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

class PrepaidDiceDataSource @Inject constructor(
    private val preferences: DataStore<Preferences>,
) {

    /**
     * Updates the status of the Prepaid Dice subscription.
     */
    suspend fun savePrepaidDiceStatus(active: Boolean) {
        withContext(Dispatchers.IO) {
            preferences.edit { prefs ->
                prefs[PREPAID_DICE_ACTIVE] = active
            }
        }
    }

    /**
     * Stream of Prepaid Dice status [Boolean].
     */
    fun getPrepaidDiceStatus(): Flow<Boolean> {
        return preferences.data.map { prefs ->
            prefs[PREPAID_DICE_ACTIVE] ?: false
        }.distinctUntilChanged()
    }

    companion object {
        val PREPAID_DICE_ACTIVE = booleanPreferencesKey("prepaid_dice_active")
    }
}
