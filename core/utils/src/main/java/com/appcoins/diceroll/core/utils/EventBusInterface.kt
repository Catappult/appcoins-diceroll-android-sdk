package com.appcoins.diceroll.core.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.onCompletion

/**
 * Simple event bus using Kotlin coroutines and channels to publish and listen to events.
 */
interface EventBusInterface {

    /**
     * The SharedFlow used for publishing and listening to events. It is recreated upon completion.
     */
    var eventFlow: MutableSharedFlow<Any>

    /**
     * Creates a new event SharedFlow with a replay` with the default buffer values.
     *
     * @return A new SharedFlow for handling events
     *
     */
    fun createEventChannel(): MutableSharedFlow<Any> {
        return MutableSharedFlow()
    }

    /**
     * Publishes an event to the event bus, so that all listeners can receive it.
     *
     * @param event The event to be published.
     */
    suspend fun publish(event: Any) {
        eventFlow.emit(event)
    }

}

/**
 * Listens for events of a specific type and provides a Flow to collect the events.
 *
 * @param T The type of event to listen for.
 * @return A Flow of events of the specified type [T].
 */
inline fun <reified T> EventBusInterface.listen(): Flow<T> {
    return eventFlow
        .filterIsInstance<T>()
        .onCompletion {
            eventFlow = createEventChannel()
        }
}
