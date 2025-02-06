package com.appcoins.diceroll.sdk.feature.settings.data.usecases

import com.appcoins.diceroll.sdk.feature.settings.data.model.User
import com.appcoins.diceroll.sdk.feature.settings.data.repository.UserDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val datastore: UserDataSource) {

    operator fun invoke(): User = runBlocking(Dispatchers.IO) { datastore.getUser().first() }
}
