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

class TrialDiceDataSource @Inject constructor(
    private val preferences: DataStore<Preferences>,
) {

    /**
     * Updates the status of the Trial Dice subscription.
     */
    suspend fun saveTrialDiceStatus(active: Boolean) {
        withContext(Dispatchers.IO) {
            preferences.edit { prefs ->
                prefs[TRIAL_DICE_ACTIVE] = active
            }
        }
    }

    /**
     * Stream of Trial Dice status [Boolean].
     */
    fun getTrialDiceStatus(): Flow<Boolean> {
        return preferences.data.map { prefs ->
            prefs[TRIAL_DICE_ACTIVE] ?: false
        }.distinctUntilChanged()
    }

    companion object {
        val TRIAL_DICE_ACTIVE = booleanPreferencesKey("trial_dice_active")
    }
}
