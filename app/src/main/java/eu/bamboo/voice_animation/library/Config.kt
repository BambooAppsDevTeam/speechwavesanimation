package eu.bamboo.voice_animation.library

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.annotation.FloatRange
import eu.bamboo.voice_animation.R

class Config(context: Context, attrs: AttributeSet?, private val musicWave: View) {

    var middleColor = 0
        set(value) {
            field = value
            paintWave.color = value
        }
    var startColor = 0
    var endColor = 0
    @FloatRange(from = 0.0, to = 0.5)
    var colorGradientPositionOffset = DEFAULT_GRADIENT_POSITION_OFFSET
    var thickness = 0f
        set(value) {
            field = value
            paintWave.strokeWidth = value
        }
    var colorGradient = false
        set(value) {
            field = value
            if (field) {
                setGradients()
            } else {
                paintWave.shader = null
            }
        }
    var paintWave = Paint()
        private set

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.VoiceWave, 0, 0)
        if (attrs != null) {
            thickness = a.getFloat(R.styleable.VoiceWave_lineThickness, 1f)
            middleColor = a.getColor(R.styleable.VoiceWave_middleColor, Color.parseColor("#691A40"))
            startColor = a.getColor(R.styleable.VoiceWave_startColor, Color.parseColor("#93278F"))
            endColor = a.getColor(R.styleable.VoiceWave_endColor, Color.parseColor("#00A99D"))
            a.recycle()
            paintWave.strokeWidth = thickness
            paintWave.isAntiAlias = true
            paintWave.style = Paint.Style.STROKE
            paintWave.color = middleColor
            paintWave.alpha = 255
        }
    }

    fun setPaintWave(paintWave: Paint): Config {
        this.paintWave = paintWave
        return this
    }

    fun setGradients() {
        paintWave.shader = LinearGradient(
            0f, 0f, 0f, musicWave.height.toFloat(),
            arrayOf(startColor, endColor, startColor).toIntArray(),
            arrayOf(
                GRADIENT_POSITION_MIDDLE - colorGradientPositionOffset,
                GRADIENT_POSITION_MIDDLE,
                GRADIENT_POSITION_MIDDLE + colorGradientPositionOffset
            ).toFloatArray(),
            Shader.TileMode.MIRROR
        )
    }

    fun reSetupPaint(): Paint {
        paintWave = Paint()
        paintWave.strokeWidth = thickness
        paintWave.isAntiAlias = true
        paintWave.style = Paint.Style.STROKE
        paintWave.color = middleColor
        paintWave.alpha = 255
        return paintWave
    }

    companion object {
        private const val GRADIENT_POSITION_MIDDLE = 0.5f
        private const val DEFAULT_GRADIENT_POSITION_OFFSET = 0.1f
    }
}