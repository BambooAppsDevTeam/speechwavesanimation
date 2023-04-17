package eu.bamboo.voice_animation

import android.content.pm.PackageManager
import android.util.Log
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.toColorInt

fun Boolean.then(block: () -> Unit) {
    if (this) {
        block.invoke()
    }
}

fun AppCompatActivity.hasPermission(permission: String) = ActivityCompat.checkSelfPermission(
    this,
    permission
) == PackageManager.PERMISSION_GRANTED

fun Int.min(min: Int) = if (this < min) min else this

private const val COLOR_PREFIX = "#"

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
            Log.d(TAG, "Unknown color exception.")
        }
    }
}

@ColorInt
fun EditText.toColorInt(): Int? = text.toString().toColorIntOrNull().apply {
    this?.let {
        error = null
    } ?: run {
        error = "Can't apply the color"
    }
}

const val TAG = "VoiceAnimation"