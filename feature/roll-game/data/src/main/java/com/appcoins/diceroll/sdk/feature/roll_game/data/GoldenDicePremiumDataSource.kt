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

class GoldenDicePremiumDataSource @Inject constructor(
    private val preferences: DataStore<Preferences>,
) {

    /**
     * Updates the status of the Golden Dice Premium subscription.
     */
    suspend fun saveGoldenDicePremiumStatus(active: Boolean) {
        withContext(Dispatchers.IO) {
            preferences.edit { prefs ->
                prefs[GOLDEN_DICE_PREMIUM_ACTIVE] = active
            }
        }
    }

    /**
     * Stream of Golden Dice Premium status [Boolean].
     */
    fun getGoldenDicePremiumStatus(): Flow<Boolean> {
        return preferences.data.map { prefs ->
            prefs[GOLDEN_DICE_PREMIUM_ACTIVE] ?: false
        }.distinctUntilChanged()
    }

    companion object {
        val GOLDEN_DICE_PREMIUM_ACTIVE = booleanPreferencesKey("golden_dice_premium_active")
    }
}
