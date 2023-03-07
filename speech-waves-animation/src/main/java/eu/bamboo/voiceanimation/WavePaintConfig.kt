package eu.bamboo.voiceanimation

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.annotation.FloatRange

class WavePaintConfig(context: Context, attrs: AttributeSet?) {

    var middleColor = 0
        set(value) {
            field = value
            paintWave.color = value
        }
    var startColor = 0
    var endColor = 0

    @FloatRange(from = 0.0, to = 0.5)
    var colorGradientPositionOffset = DEFAULT_GRADIENT_POSITION_OFFSET
    var thickness = DEFAULT_THICKNESS
    var thicknessMiddle = DEFAULT_THICKNESS_MIDDLE
    var paintWave = Paint()
        private set

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.VoiceWave, 0, 0)
        if (attrs != null) {
            thickness = a.getFloat(R.styleable.VoiceWave_lineThickness, DEFAULT_THICKNESS)
            thicknessMiddle = a.getFloat(R.styleable.VoiceWave_middleLineThickness, DEFAULT_THICKNESS_MIDDLE)
            middleColor = a.getColor(R.styleable.VoiceWave_middleColor, Color.parseColor("#691A40"))
            startColor = a.getColor(R.styleable.VoiceWave_startColor, Color.parseColor("#93278F"))
            endColor = a.getColor(R.styleable.VoiceWave_endColor, Color.parseColor("#00A99D"))
            colorGradientPositionOffset = a.getFloat(R.styleable.VoiceWave_gradientOffset, DEFAULT_GRADIENT_POSITION_OFFSET)
            a.recycle()
            updatePaint()
        }
    }

    fun updatePaint() {
        paintWave = Paint()
        paintWave.strokeWidth = thickness
        paintWave.isAntiAlias = true
        paintWave.style = Paint.Style.STROKE
        paintWave.color = middleColor
        paintWave.alpha = 255
    }

    fun setMainLine(isMainLine: Boolean, view: View) {
        if (isMainLine) {
            setGradients(view)
        } else {
            paintWave.shader = null
        }
        paintWave.strokeWidth = (if (isMainLine) thickness else thicknessMiddle).toFloat()
    }

    private fun setGradients(view: View) {
        paintWave.shader = LinearGradient(
            0f, 0f, 0f, view.height.toFloat(),
            arrayOf(startColor, endColor, startColor).toIntArray(),
            arrayOf(
                GRADIENT_POSITION_MIDDLE - colorGradientPositionOffset,
                GRADIENT_POSITION_MIDDLE,
                GRADIENT_POSITION_MIDDLE + colorGradientPositionOffset
            ).toFloatArray(),
            Shader.TileMode.MIRROR
        )
    }

    companion object {
        private const val GRADIENT_POSITION_MIDDLE = 0.5f
        private const val DEFAULT_GRADIENT_POSITION_OFFSET = 0.1f
        private const val DEFAULT_THICKNESS = 6f
        private const val DEFAULT_THICKNESS_MIDDLE = 3f
    }
}