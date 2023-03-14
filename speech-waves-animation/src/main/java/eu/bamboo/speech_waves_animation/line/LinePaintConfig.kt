package eu.bamboo.speech_waves_animation.line

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import eu.bamboo.speech_waves_animation.R

class LinePaintConfig(context: Context, attrs: AttributeSet?) {

    val colorList = IntArray(COLOR_COUNT_MAX) { Color.LTGRAY }

    var baseColor = 0

    var paintWave = Paint()
        private set

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.VoiceLine, 0, 0)
        if (attrs != null) {
            baseColor = a.getColor(R.styleable.VoiceLine_baseColor, Color.parseColor("#1D4A76"))

            colorList[0] = a.getColor(R.styleable.VoiceLine_color1, Color.parseColor("#4493E2"))
            colorList[1] = a.getColor(R.styleable.VoiceLine_color2, Color.parseColor("#1A5B9C"))
            colorList[2] = a.getColor(R.styleable.VoiceLine_color3, Color.RED)
            colorList[3] = a.getColor(R.styleable.VoiceLine_color4, Color.WHITE)
            colorList[4] = a.getColor(R.styleable.VoiceLine_color5, Color.GREEN)
            colorList[5] = a.getColor(R.styleable.VoiceLine_color6, Color.CYAN)
            colorList[6] = a.getColor(R.styleable.VoiceLine_color7, Color.YELLOW)
            colorList[7] = a.getColor(R.styleable.VoiceLine_color8, Color.GRAY)

            a.recycle()
            updatePaint()
        }
    }

    fun updatePaint() {
        paintWave = Paint()
        paintWave.isAntiAlias = true
        paintWave.color = baseColor
    }

    fun setColor(color: Int) {
        paintWave.color = color
    }

    companion object {
        private const val COLOR_COUNT_MAX = 8
    }
}