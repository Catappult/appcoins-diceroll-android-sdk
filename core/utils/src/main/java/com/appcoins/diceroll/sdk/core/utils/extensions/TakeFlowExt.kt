package com.appcoins.diceroll.sdk.core.utils.extensions

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.time.Duration

fun <T, R> Flow<T>.repeatUntil(
  map: suspend (T) -> R,
  predicate: suspend FlowCollector<R>.(item: T, result: R, attempt: Long) -> Boolean
): Flow<R> {

  tailrec suspend fun FlowCollector<R>.retryCollect(item: T, attempt: Long) {
    val result = map(item)
    if (predicate(item, result, attempt)) {
      retryCollect(item, attempt = attempt + 1)
    } else {
      emit(result)
    }
  }

  return flow {
    collect { item ->
      retryCollect(item, attempt = 1L)
    }
  }
}

fun <T> Flow<T>.takeUntilTimeout(interval: Duration) = channelFlow {
  val collector = launch {
    collect {
      send(it)
    }
    close()
  }
  delay(interval)
  collector.cancel()
  close(Throwable("Timed out waiting for $interval"))
}