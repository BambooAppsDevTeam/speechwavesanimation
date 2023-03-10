package eu.bamboo.speech_waves_animation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IntRange
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class SpeechLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var rawAudioBytes: ByteArray? = null
    private var maxBatchCount = MAX_ANIM_BATCH_COUNT
    private val rect = Rect()

    private val wavePaintConfig: WavePaintConfig = WavePaintConfig(context, attrs)

    private var pathList: Array<Path> = emptyArray()
    private var linesOffset = 1f
    @IntRange(from = 1, to = 8)
    var pathCount = DEFAULT_PATH_COUNT
        set(value) {
            field = value
            pathList = Array(value) { Path() }
            linesOffset = if (value == 1) 1f else 2f / (value - 1)
        }
    var speed: AnimationSpeed = AnimationSpeed.NORMAL
        set(value) {
            field = value
            maxBatchCount = MAX_ANIM_BATCH_COUNT - field.ordinal
        }

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.VoiceWave, 0, 0)
        if (attrs != null) {
            speed = a.getColor(R.styleable.VoiceWave_animationSpeed, AnimationSpeed.NORMAL.ordinal).toAnimationSpeed()
            pathCount = a.getColor(R.styleable.VoiceWave_lineCount, DEFAULT_PATH_COUNT)
            a.recycle()
        }
    }

    fun updateVisualizer(bytes: ByteArray?) {
        this.rawAudioBytes = bytes
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val bytes = rawAudioBytes ?: return

        rect.set(0, 0, width, height)

        wavePaintConfig.updatePaint()

        drawPath(canvas)
    }

    private val colors = arrayOf(Color.BLUE, Color.CYAN, Color.MAGENTA, Color.DKGRAY, Color.YELLOW, Color.BLACK, Color.LTGRAY, Color.GRAY)

    private fun drawPath(canvas: Canvas) {
        val bytes = rawAudioBytes?.filter { abs(it.toInt()) < BYTE_SIZE } ?: return
        val batchCount = bytes.count()
        val batch2Count = BYTE_SIZE / pathCount
        val width = rect.width().toFloat()
        val widthCenter = width / 2
        val heightCenter = 0f //rect.height() / 2f
        val density = width / batchCount
        val leftBytes = bytes.filter { it >= 0 }
        val rightBytes = bytes.filter { it <= 0 }

        wavePaintConfig.setMainLine(false, this)
        wavePaintConfig.middleColor = Color.RED
//        wavePaintConfig.paintWave.style = Paint.Style.FILL
        canvas.drawLine(0f, heightCenter, width, heightCenter, wavePaintConfig.paintWave)
//        canvas.drawRect(0f, heightCenter, width, rect.height().toFloat(), wavePaintConfig.paintWave)

        wavePaintConfig.paintWave.style = Paint.Style.STROKE
        pathList.forEachIndexed { index, path ->
            path.rewind()

            val start = leftBytes.count { it > batch2Count * index }
            val end = rightBytes.count { it < -batch2Count * index }
            val startX = max(widthCenter - start * density, 0f)
            val endX = min(widthCenter + end * density, width)

            path.moveTo(startX, heightCenter + index * 16)
            path.lineTo(endX, heightCenter + index * 16)

            wavePaintConfig.middleColor = colors[index]

            canvas.drawPath(path, wavePaintConfig.paintWave)
        }
    }

    companion object {
        private const val MAX_ANIM_BATCH_COUNT = 4
        private const val DEFAULT_PATH_COUNT = 4
        private const val BYTE_SIZE = 128
    }
}
