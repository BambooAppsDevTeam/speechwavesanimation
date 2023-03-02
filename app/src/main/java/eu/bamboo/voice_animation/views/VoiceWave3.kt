package eu.bamboo.voice_animation.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import eu.bamboo.voice_animation.R
import eu.bamboo.voice_animation.algorithm.Point
import eu.bamboo.voice_animation.library.Config
import eu.bamboo.voice_animation.library.firstOrLast
import kotlin.math.ceil

class VoiceWave3 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var rawAudioBytes: ByteArray? = null
    private var pointCount: Int = EXTREMUM_NUMBER_MAX
    private var points: Array<Point> = Array(pointCount + 1) { Point(0f, 0f) }
    private var bezierControlStartPoints: Array<Point> = Array(pointCount + 1) { Point(0f, 0f) }
    private var bezierControlEndPoints: Array<Point> = Array(pointCount + 1) { Point(0f, 0f) }
    private var sourceY: FloatArray = FloatArray(pointCount + 1)
    private var destinationY: FloatArray = FloatArray(pointCount + 1)
    private var maxBatchCount = 0
    private var batchCount = 0
    private val rect = Rect()
    private var widthOffset = -1f
    private var windowPadding = DEFAULT_PADDING
    private var config: Config = Config(context, attrs, this)

    private var pathList: Array<Path> = emptyArray()
    private var linesOffset = 1f
    private var pathCount = 1
        set(value) {
            field = value
            pathList = Array(value) { Path() }
            linesOffset = if (value == 1) 1f else 2f / (value - 1)
        }
    private val bezierControlStartPointsPath = Path()
    private val bezierControlEndPointsPath = Path()
    private val top = Path()
    private val center = Path()
    private val bottom = Path()
    private var density = DEFAULT_DENSITY
        set(value) {
            field = value
            pointCount = (EXTREMUM_NUMBER_MAX * field).toInt()
            if (pointCount < EXTREMUM_NUMBER_MIN) pointCount = EXTREMUM_NUMBER_MIN
            createArraysIfChanged()
            widthOffset = -1f
        }
    private var speed: AnimationSpeed = AnimationSpeed.NORMAL
        set(value) {
            field = value
            maxBatchCount = MAX_ANIM_BATCH_COUNT - field.ordinal
        }

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.VoiceWave, 0, 0)
        if (attrs != null) {
            density = a.getFloat(R.styleable.VoiceWave_waveDensity, DEFAULT_DENSITY)
            speed = a.getColor(R.styleable.VoiceWave_animationSpeed, AnimationSpeed.NORMAL.ordinal).toAnimationSpeed()
            pathCount = a.getColor(R.styleable.VoiceWave_lineCount, 1)
            windowPadding = a.getFloat(R.styleable.VoiceWave_windowPadding, DEFAULT_PADDING)
            a.recycle()
        }
        createArraysIfChanged()
    }

    fun updateVisualizer(bytes: ByteArray?) {
        this.rawAudioBytes = bytes
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val bytes = rawAudioBytes ?: return

        rect.set(0, 0, width, height)

        initializeBezierPoints()
        findDestinationBezierPointForBatch(bytes)
        smoothAnimation()
        calculateBezierCurveControlPoints()

        prepareConfig()

        drawPath(canvas)
    }

    private fun createArraysIfChanged() {
        points = Array(pointCount + 1) { Point(0f, 0f) }
        bezierControlStartPoints = Array(pointCount + 1) { Point(0f, 0f) }
        bezierControlEndPoints = Array(pointCount + 1) { Point(0f, 0f) }
        sourceY = FloatArray(pointCount + 1)
        destinationY = FloatArray(pointCount + 1)
    }

    private fun initializeBezierPoints() {
        val heightCenter = rect.height() / 2
        if (widthOffset == -1f) {
            widthOffset = (rect.width() / pointCount).toFloat()

            for (i in points.indices) {
                val posX = rect.left + i * widthOffset
                val posY = heightCenter.toFloat()
                sourceY[i] = posY
                destinationY[i] = posY
                points[i].x = posX
                points[i].y = posY
            }
        }
    }

    private fun findDestinationBezierPointForBatch(rawAudioBytes: ByteArray) {
        val heightCenter = rect.height() / 2
        val paddingHorizontal = AXIS_X_WIDTH * windowPadding
        if (batchCount == 0) {
            val randPosY = destinationY.last()
            for (i in points.indices) {
                val x = ceil(((i + 1) * (rawAudioBytes.size / pointCount)).toDouble()).toInt()
                val posY = if (x > paddingHorizontal && x < AXIS_X_WIDTH - paddingHorizontal) {
                    heightCenter + (rawAudioBytes[x] + BYTE_SIZE).toByte() * heightCenter / BYTE_SIZE
                } else {
                    heightCenter
                }

                sourceY[i] = destinationY[i]
                destinationY[i] = posY.toFloat()
            }
            destinationY[points.size - 1] = randPosY
        }
    }

    private fun smoothAnimation() {
        batchCount++

        for (i in points.indices) {
            points[i].y = sourceY[i] + batchCount.toFloat() / maxBatchCount * (destinationY[i] - sourceY[i])
        }

        if (batchCount == maxBatchCount) batchCount = 0
    }

    private fun calculateBezierCurveControlPoints() {
        for (i in 1 until points.size) {
            val bezierControlX = (points[i].x + points[i - 1].x) / 2
            bezierControlStartPoints[i].x = bezierControlX
            bezierControlStartPoints[i].y = points[i - 1].y
            bezierControlEndPoints[i].x = bezierControlX
            bezierControlEndPoints[i].y = points[i].y
        }
    }

    private fun prepareConfig() {
        if (config.colorGradient) {
            config.reSetupPaint()
            config.colorGradient = true
        } else {
            config.reSetupPaint()
        }
    }

    private fun drawPath(canvas: Canvas) {
        pathList
            .filterIndexed { index, path -> index == 0 }
            .forEachIndexed { index, path ->
                path.rewind()
                bezierControlStartPointsPath.rewind()
                bezierControlEndPointsPath.rewind()

                val coefficient = 1 - index * linesOffset
                path.moveTo(points[0].x, getRelativeY(points[0].y, coefficient))
                for (i in 1 until points.size) {
                    path.cubicTo(
                        bezierControlStartPoints[i].x,
                        getRelativeY(bezierControlStartPoints[i].y, coefficient),
                        bezierControlEndPoints[i].x,
                        getRelativeY(bezierControlEndPoints[i].y, coefficient),
                        points[i].x,
                        getRelativeY(points[i].y, coefficient)
                    )
                    bezierControlStartPointsPath.addCircle(
                        bezierControlStartPoints[i].x,
                        getRelativeY(bezierControlStartPoints[i].y, coefficient),
                        10f,
                        Path.Direction.CW
                    )
                    bezierControlEndPointsPath.addCircle(
                        bezierControlEndPoints[i].x,
                        getRelativeY(bezierControlEndPoints[i].y, coefficient),
                        10f,
                        Path.Direction.CW
                    )
                }

                if (pathList.firstOrLast(index)) {
                    config.thickness = 6f
                    config.colorGradient = true
                } else {
                    config.thickness = 2f
                    config.colorGradient = false
                }
                canvas.drawPath(path, config.paintWave)

                config.colorGradient = false
                config.middleColor = Color.RED
                canvas.drawPath(bezierControlStartPointsPath, config.paintWave)
                config.middleColor = Color.YELLOW
                canvas.drawPath(bezierControlEndPointsPath, config.paintWave)

                config.thickness = 3f
                top.moveTo(rect.left.toFloat(), rect.top.toFloat())
                top.lineTo(rect.right.toFloat(), rect.top.toFloat())
                config.middleColor = Color.BLACK
                canvas.drawPath(top, config.paintWave)

                center.moveTo(rect.left.toFloat(), rect.height().toFloat() / 2)
                center.lineTo(rect.right.toFloat(), rect.height().toFloat() / 2)
                config.middleColor = Color.CYAN
                canvas.drawPath(center, config.paintWave)

                bottom.moveTo(rect.left.toFloat(), rect.bottom.toFloat())
                bottom.lineTo(rect.right.toFloat(), rect.bottom.toFloat())
                config.middleColor = Color.MAGENTA
                canvas.drawPath(bottom, config.paintWave)
            }
    }

    private fun getRelativeY(y: Float, coefficient: Float): Float {
        val heightCenter = rect.height() / 2
        val diff = y - heightCenter
        return heightCenter + diff * coefficient
    }

    fun setConfig(config: Config): VoiceWave3 {
        this.config = config
        return this
    }

    enum class AnimationSpeed {
        SLOW, NORMAL, FAST
    }

    private fun Int.toAnimationSpeed(default: AnimationSpeed = AnimationSpeed.NORMAL): AnimationSpeed {
        return AnimationSpeed.values().find { it.ordinal == this } ?: default
    }

    companion object {
        private const val EXTREMUM_NUMBER_MAX = 54
        private const val EXTREMUM_NUMBER_MIN = 3
        private const val DEFAULT_DENSITY = 0.1f
        private const val MAX_ANIM_BATCH_COUNT = 4
        private const val AXIS_X_WIDTH = 1024
        private const val DEFAULT_PADDING = 0.15f
    }
}
