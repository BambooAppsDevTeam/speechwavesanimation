package eu.bamboo.speech_waves_animation.line

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IntRange
import eu.bamboo.speech_waves_animation.AnimationSpeed
import eu.bamboo.speech_waves_animation.R
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
    private var batchCount = 0
    private val rect = Rect()

    val linePaintConfig: LinePaintConfig = LinePaintConfig(context, attrs)

    @IntRange(from = 1, to = 8)
    var lineCount = DEFAULT_PATH_COUNT

    var symmetry = true

    var speed: AnimationSpeed = AnimationSpeed.NORMAL
        set(value) {
            field = value
            maxBatchCount = MAX_ANIM_BATCH_COUNT - field.ordinal
        }

    private var prevAmplitudes: List<Pair<Int, Int>> = emptyList()
    private var currentAmplitudes: List<Pair<Int, Int>> = emptyList()
    private var amplitudes: MutableList<Pair<Float, Float>> = mutableListOf()

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.VoiceLine, 0, 0)
        if (attrs != null) {
//            speed = a.getColor(R.styleable.VoiceLine_animationSpeed, AnimationSpeed.NORMAL.ordinal).toAnimationSpeed()
            symmetry = a.getBoolean(R.styleable.VoiceLine_symmetry, true)
            lineCount = a.getColor(R.styleable.VoiceLine_lineCount, DEFAULT_PATH_COUNT)
            a.recycle()
        }
    }

    fun updateVisualizer(bytes: ByteArray?) {
        this.rawAudioBytes = bytes
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        rect.set(0, 0, width, height)

        linePaintConfig.updatePaint()

        drawBackground(canvas)

        val bytes = rawAudioBytes?.filter { abs(it.toInt()) < BYTE_SIZE } ?: return

        calculateAmplitudes(bytes)
        smoothAnimation()
        drawAmplitudes(canvas, bytes)
    }

    private fun drawBackground(canvas: Canvas) {
        val width = rect.width().toFloat()
        val height = rect.height().toFloat()

        linePaintConfig.paintWave.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, width, height, linePaintConfig.paintWave)
    }

//    private val fibonacci = byteArrayOf(1, 2, 3, 5, 8, 13, 21, 34)

    private fun calculateAmplitudes(bytes: List<Byte>) {
        val temp = 8
        val batchCount = BYTE_SIZE / (lineCount + temp)
//        val fibonacci = this.fibonacci.take(pathCount)
//        val fibonacciBatchCount = fibonacci.sum()
//        val batchCount = BYTE_SIZE / fibonacciBatchCount.toFloat()
//        Log.d("Ololo", "batchCount = $batchCount")
//        Log.d("Ololo", "fibonacci = ${fibonacci.joinToString()}")
//        Log.d("Ololo", "fibonacciBatchCount = $fibonacciBatchCount")
//        Log.d("Ololo", "pathCount = $pathCount")
//        Log.d("Ololo", "bytes = $bytes")
        val barList = mutableListOf<Pair<Int, Int>>()
        for (index in 0 until lineCount) {
            val start: Int
            val end: Int

            if (symmetry) {
                start = bytes.count { abs(it.toInt()) > batchCount * (index + temp) }
                end = start
            } else {
                start = bytes.count { it > batchCount * (index + temp) }
                end = bytes.count { it < -batchCount * (index + temp) }
            }
//            val sum = fibonacci.take(index + 1).sum()
//            Log.d("Ololo", "sum = $sum")
//            val i = 128 - batchCount * sum
//            Log.d("Ololo", "i = $i")
//            val start = bytes.count { it > i }
//            val end = bytes.count { it < -i }
            barList.add(Pair(start, end))
        }
        prevAmplitudes = currentAmplitudes
        currentAmplitudes = barList
        if (prevAmplitudes.isEmpty()) {
            prevAmplitudes = currentAmplitudes
        }
    }

    private fun smoothAnimation() {
        batchCount++
        amplitudes.clear()

        for (i in currentAmplitudes.indices) {
            val amplitudeLeft = prevAmplitudes[i].first + batchCount.toFloat() / maxBatchCount * (currentAmplitudes[i].first - prevAmplitudes[i].first)
            val amplitudeRight = prevAmplitudes[i].second + batchCount.toFloat() / maxBatchCount * (currentAmplitudes[i].second - prevAmplitudes[i].second)
            amplitudes.add(Pair(amplitudeLeft, amplitudeRight))
        }

        if (batchCount == maxBatchCount) batchCount = 0
    }

    private fun drawAmplitudes(canvas: Canvas, bytes: List<Byte>) {
        val batchCount = bytes.count()
        val width = rect.width().toFloat()
        val height = rect.height().toFloat()
        val widthCenter = width / 2
        val density = width / batchCount

        amplitudes.forEachIndexed { index, pair ->
            val startX = max(widthCenter - pair.first * density, 0f)
            val endX = min(widthCenter + pair.second * density, width)

            linePaintConfig.setColor(linePaintConfig.colorList[index])

            canvas.drawRect(startX, 0f, endX, height, linePaintConfig.paintWave)
        }
    }

    companion object {
        private const val MAX_ANIM_BATCH_COUNT = 4
        private const val DEFAULT_PATH_COUNT = 2
        private const val BYTE_SIZE = 128
    }
}
