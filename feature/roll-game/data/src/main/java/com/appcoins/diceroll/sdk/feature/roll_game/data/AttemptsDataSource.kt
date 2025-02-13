package com.appcoins.diceroll.sdk.feature.roll_game.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.appcoins.diceroll.sdk.feature.roll_game.data.PreferencesKeys.ATTEMPTS_LEFT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AttemptsDataSource @Inject constructor(
    private val preferences: DataStore<Preferences>,
) {

    /**
     * Sets the desired attempts left.
     */
    suspend fun saveAttemptsLeft(attemptsLeft: Int) {
        withContext(Dispatchers.IO) {
            preferences.edit { prefs ->
                prefs[ATTEMPTS_LEFT] = attemptsLeft
            }
        }
    }

    /**
     * Sets the desired attempts left.
     */
    suspend fun addAttempts(attemptsToAdd: Int) {
        withContext(Dispatchers.IO) {
            getAttemptsLeft().firstOrNull()?.let { attemptsLeft ->
                preferences.edit { prefs ->
                    prefs[ATTEMPTS_LEFT] = attemptsLeft + attemptsToAdd
                }
            }
        }
    }

    /**
     * Stream of attempts left [Int].
     */
    fun getAttemptsLeft(): Flow<Int> {
        return preferences.data.map { prefs ->
            prefs[ATTEMPTS_LEFT] ?: DEFAULT_ATTEMPTS_NUMBER
        }.distinctUntilChanged()
    }
}

object PreferencesKeys {
    val ATTEMPTS_LEFT = intPreferencesKey("attempts_left")
}

const val DEFAULT_ATTEMPTS_NUMBER = 3
