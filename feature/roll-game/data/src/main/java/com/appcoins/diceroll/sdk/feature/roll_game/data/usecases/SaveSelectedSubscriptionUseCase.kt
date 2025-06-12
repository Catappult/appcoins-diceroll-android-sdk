package com.appcoins.diceroll.sdk.feature.roll_game.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.SubscriptionsDataSource
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription
import javax.inject.Inject

class SaveSelectedSubscriptionUseCase @Inject constructor(private val datastore: SubscriptionsDataSource) {

    suspend operator fun invoke(subscription: Subscription) =
        datastore.saveSelectedSubscription(subscription)
}
