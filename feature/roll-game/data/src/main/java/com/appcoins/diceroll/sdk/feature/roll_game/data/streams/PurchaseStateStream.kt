package com.appcoins.diceroll.sdk.feature.roll_game.data.streams

import com.appcoins.diceroll.sdk.core.utils.EventBusInterface
import kotlinx.coroutines.flow.MutableSharedFlow

object DiceSelectionDialogVisibilityStateStream : EventBusInterface {

    override var eventFlow = MutableSharedFlow<Any>()
}
