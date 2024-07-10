package com.appcoins.diceroll.core.ui.widgets

import kotlin.math.atan2

fun touchPointToAngle(
    width: Float,
    height: Float,
    touchX: Float,
    touchY: Float,
    chartDegrees: Float
): Double {
    val x = touchX - (width * 0.5f)
    val y = touchY - (height * 0.5f)
    var angle = Math.toDegrees(atan2(y.toDouble(), x.toDouble()) + Math.PI / 2)
    angle = if (angle < 0) angle + chartDegrees else angle
    return angle
}