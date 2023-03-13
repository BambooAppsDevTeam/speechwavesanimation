package eu.bamboo.speech_waves_animation.line

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import eu.bamboo.speech_waves_animation.R

class LinePaintConfig(context: Context, attrs: AttributeSet?) {

    var color = 0
        set(value) {
            field = value
            paintWave.color = value
        }

    var paintWave = Paint()
        private set

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.VoiceLine, 0, 0)
        if (attrs != null) {
            color = a.getColor(R.styleable.VoiceLine_color, Color.BLUE)
            a.recycle()
            updatePaint()
        }
    }

    fun updatePaint() {
        paintWave = Paint()
        paintWave.isAntiAlias = true
        paintWave.color = color
    }
}