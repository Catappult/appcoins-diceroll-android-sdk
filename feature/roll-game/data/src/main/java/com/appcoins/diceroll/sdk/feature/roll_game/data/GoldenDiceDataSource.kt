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

class GoldenDiceDataSource @Inject constructor(
    private val preferences: DataStore<Preferences>,
) {

    /**
     * Updates the status of the Golden Dice subscription.
     */
    suspend fun saveGoldenDiceStatus(active: Boolean) {
        withContext(Dispatchers.IO) {
            preferences.edit { prefs ->
                prefs[GOLDEN_DICE_ACTIVE] = active
            }
        }
    }

    /**
     * Stream of Golden Dice status [Boolean].
     */
    fun getGoldenDiceStatus(): Flow<Boolean> {
        return preferences.data.map { prefs ->
            prefs[GOLDEN_DICE_ACTIVE] ?: false
        }.distinctUntilChanged()
    }

    companion object {
        val GOLDEN_DICE_ACTIVE = booleanPreferencesKey("golden_dice_active")
    }
}
