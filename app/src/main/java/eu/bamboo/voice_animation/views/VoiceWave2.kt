package eu.bamboo.voice_animation.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import eu.bamboo.voice_animation.algorithm.Point
import kotlin.math.abs

class VoiceWave2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var rawAudioBytes: ByteArray? = null
    private var pointCount: Int = EXTREMUM_NUMBER_MAX
    private var points: Array<Point> = Array(pointCount) { Point(0f, 0f) }
    private var bezierControlStartPoints: Array<Point> = Array(pointCount) { Point(0f, 0f) }
    private var bezierControlEndPoints: Array<Point> = Array(pointCount) { Point(0f, 0f) }
    private val rect = Rect()
    private var config: Config = Config(context, attrs, this)
    private val path = Path()
    private var density = DEFAULT_DENSITY
        set(value) {
            field = value
            pointCount = (EXTREMUM_NUMBER_MAX * field).toInt()
            if (pointCount < EXTREMUM_NUMBER_MIN) pointCount = EXTREMUM_NUMBER_MIN
            points = Array(pointCount) { Point(0f, 0f) }
            bezierControlStartPoints = Array(pointCount) { Point(0f, 0f) }
            bezierControlEndPoints = Array(pointCount) { Point(0f, 0f) }
        }

    fun updateVisualizer(bytes: ByteArray?) {
        this.rawAudioBytes = bytes
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val bytes = rawAudioBytes ?: return
        if (points.size != pointCount) {
            points = Array(pointCount) { Point(0f, 0f) }
            bezierControlStartPoints = Array(pointCount) { Point(0f, 0f) }
            bezierControlEndPoints = Array(pointCount) { Point(0f, 0f) }
        }

        rect[0, 0, width] = height
//        val width = rect.width()
        val widthOffset = (rect.width() / pointCount).toFloat()
        val heightCenter = rect.height() / 2
        for (i in 0 until pointCount - 1) {
            points[i].x = rect.left + i * widthOffset
//            points[i].x = (width * i / pointCount).toFloat()
//            points[i].x = (width * i / (bytes.size - 1)).toFloat()
            // bytes[i] - as it is
            // abs(bytes[i].toInt()) - reverse negative
            // if (bytes[i] < 0) bytes[i] = 0 - avoid negative
            val byteIndex = bytes.size / pointCount * i
            val byte1 = abs(bytes[byteIndex].toInt()).toByte()
            val byte2 = bytes[byteIndex]
            val byte = if (bytes[byteIndex] >= 0) bytes[byteIndex] else (BYTE_SIZE - 1).toByte()
            points[i].y = (heightCenter + (byte2 + BYTE_SIZE).toByte() * (heightCenter) / BYTE_SIZE).toFloat()
        }

        prepareConfig()
        path.rewind()

//        path.moveTo(0f, BYTE_SIZE - 1f)
//
//        points
//            .dropLast(1)
//            .filterMaximum(5)
////            .findExtremums()
////            .separateLineBy()
////            .filterIndexed { index, _ -> index % 1 == 0 }
//            .forEach { point ->
//                val x = point.x
//                val y = point.y
//                path.lineTo(x, y)
//            }
//        path.lineTo(width.toFloat(), BYTE_SIZE - 1f)
//        points
//            .dropLast(2)
//            .filterIndexed { index, _ -> index % 1 == 0 }
//            .asSequence()
//            .chunked(2)
//            .forEach { points ->
//                path.quadTo(
//                    points[0].x,
//                    points[0].y,
//                    points[1].x,
//                    points[1].y
//                )
//            }

        //calculate the bezier curve control points
        for (i in 1 until points.size) {
            bezierControlStartPoints[i].x = (points[i].x + points[i - 1].x) / 2
            bezierControlStartPoints[i].y = points[i - 1].y
            bezierControlEndPoints[i].x = (points[i].x + points[i - 1].x) / 2
            bezierControlEndPoints[i].y = points[i].y
        }

        //create the path
        path.moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size - 1) {
            path.cubicTo(
                bezierControlStartPoints[i].x, bezierControlStartPoints[i].y,
                bezierControlEndPoints[i].x, bezierControlEndPoints[i].y,
                points[i].x, points[i].y
            )
        }

        canvas.drawPath(path, config.paintWave)
    }

    private fun prepareConfig() {
        if (config.colorGradient) {
            config.reSetupPaint()
            config.setGradients()
        } else {
            config.reSetupPaint()
        }
    }

    fun setConfig(config: Config): VoiceWave2 {
        this.config = config
        return this
    }

    private fun  List<Point>.filterMaximum(number: Int): List<Point> {
        val heightCenter = rect.height() / 2
        return asSequence()
            .chunked((this.size / number))
            .mapNotNull { chunk ->
                chunk
                    .map { point ->
                        val relativeY = if (point.y > heightCenter) {
                            val diff = point.y - heightCenter
                            heightCenter - diff
                        } else {
                            point.y
                        }
                        point to relativeY
                    }
                    .minByOrNull { (_, relativeY) -> relativeY }
                    ?.first
            }
            .sortedBy { it.x }
            .toList()
    }

    companion object {
        private const val EXTREMUM_NUMBER_MAX = 54
        private const val EXTREMUM_NUMBER_MIN = 3
        private const val DEFAULT_DENSITY = 0.2f
    }
}

/**
 * Byte array from Visualizer has values [-128, 128]
 */
const val BYTE_SIZE = 128

private fun  List<Point>.separateLineBy(): List<Point> {
    val result = mutableListOf<Point>()
    for (i in indices) {
        result.add(this[i])

        if (i != indices.last) {
            val x = (this[i].x + this[i + 1].x) / 2
            val point = Point(x, BYTE_SIZE - 1f)
            result.add(point)
        }
    }
    return result
}