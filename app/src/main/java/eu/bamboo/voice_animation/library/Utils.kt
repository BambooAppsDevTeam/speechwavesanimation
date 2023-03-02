package eu.bamboo.voice_animation.library

import android.util.Log
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.core.graphics.toColorInt

fun <T> Array<T>.firstOrLast(index: Int): Boolean = index == 0 || index == size - 1

fun Int.toAnimationSpeed(default: VoiceWave.AnimationSpeed = VoiceWave.AnimationSpeed.NORMAL): VoiceWave.AnimationSpeed {
    return VoiceWave.AnimationSpeed.values().find { it.ordinal == this } ?: default
}

fun Int.min(min: Int) = if (this < min) min else this

@ColorInt
fun String.toColorIntOrNull(): Int? = try {
    val color = if (this.startsWith(COLOR_PREFIX)) {
        this
    } else {
        "$COLOR_PREFIX$this"
    }
    color.toColorInt()
} catch (e: Exception) {
    when (e) {
        is NumberFormatException, is IllegalArgumentException -> null
        else -> {
            Log.d("VoiceActivity", "Unknown color exception.")
        }
    }
}

private const val COLOR_PREFIX = "#"

@ColorInt
fun EditText.toColorInt(): Int? = text.toString().toColorIntOrNull().apply {
    this?.let {
        error = null
    } ?: run {
        error = "Unknown color"
    }
}