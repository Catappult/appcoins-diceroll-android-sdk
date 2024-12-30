package com.appcoins.diceroll.sdk.feature.settings.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.appcoins.diceroll.sdk.feature.settings.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class UserDataSource @Inject constructor(
    private val preferences: DataStore<Preferences>,
) {

    /**
     * Stream of [User]
     */
    fun getUser(): Flow<User> {
        return preferences.data.map { prefs ->
            val uuid = prefs[USER_UUID] ?: generateAndSaveUUID()
            User(uuid = uuid)
        }.distinctUntilChanged()
    }

    /**
     * Sets the UUID of the User.
     */
    private suspend fun saveUUID(uuid: String) {
        withContext(Dispatchers.IO) {
            preferences.edit { prefs ->
                prefs[USER_UUID] = uuid
            }
        }
    }

    private fun generateAndSaveUUID(): String {
        val uuid = "3ad61aa1-3111-468b-85a6-fe36d56785fe"//UUID.randomUUID().toString()
        CoroutineScope(Dispatchers.IO).launch {
            saveUUID(uuid)
        }
        return uuid
    }

    private companion object {
        val USER_UUID = stringPreferencesKey("user_uuid")
    }
}
