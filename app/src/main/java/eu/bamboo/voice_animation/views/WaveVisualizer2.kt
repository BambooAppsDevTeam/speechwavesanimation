package eu.bamboo.voice_animation.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.util.AttributeSet
import com.gauravk.audiovisualizer.base.BaseVisualizer
import com.gauravk.audiovisualizer.model.AnimSpeed
import com.gauravk.audiovisualizer.model.PaintStyle
import com.gauravk.audiovisualizer.utils.AVConstants
import java.util.Random
import kotlin.math.abs
import kotlin.math.ceil

class WaveVisualizer2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseVisualizer(context, attrs, defStyleAttr) {

    private var mMaxBatchCount = 0
    private var mWavePath: Path = Path()
    private var nPoints = 0
    private var mBezierPoints: Array<PointF> = Array(nPoints + 1) { PointF(0f, 0f) }
    private var mBezierControlPoints1: Array<PointF> = Array(nPoints + 1) { PointF(0f, 0f) }
    private var mBezierControlPoints2: Array<PointF> = Array(nPoints + 1) { PointF(0f, 0f) }
    private var mSrcY: FloatArray = FloatArray(nPoints + 1)
    private var mDestY: FloatArray = FloatArray(nPoints + 1)
    private var mWidthOffset = -1f
    private var mClipBounds: Rect = Rect()
    private var nBatchCount = 0
    private var mRandom: Random = Random()

    override fun init() {
        nPoints = (WAVE_MAX_POINTS * mDensity).toInt()
        if (nPoints < WAVE_MIN_POINTS) nPoints = WAVE_MIN_POINTS
        mWidthOffset = -1f
        nBatchCount = 0
        setAnimationSpeed(mAnimSpeed)
        mRandom = Random()
        mClipBounds = Rect()
        mWavePath = Path()
        mSrcY = FloatArray(nPoints + 1)
        mDestY = FloatArray(nPoints + 1)

        mBezierPoints = Array(nPoints + 1) { PointF(0f, 0f) }
        mBezierControlPoints1 = Array(nPoints + 1) { PointF(0f, 0f) }
        mBezierControlPoints2 = Array(nPoints + 1) { PointF(0f, 0f) }
        for (i in mBezierPoints.indices) {
            mBezierPoints[i] = PointF()
            mBezierControlPoints1[i] = PointF()
            mBezierControlPoints2[i] = PointF()
        }
    }

    override fun setAnimationSpeed(animSpeed: AnimSpeed) {
        super.setAnimationSpeed(animSpeed)
        mMaxBatchCount = AVConstants.MAX_ANIM_BATCH_COUNT - mAnimSpeed.ordinal
    }

    override fun onDraw(canvas: Canvas) {
        val heightCenter = canvas.height / 2
        if (mWidthOffset == -1f) {
            canvas.getClipBounds(mClipBounds)
            mWidthOffset = (canvas.width / nPoints).toFloat()

            for (i in mBezierPoints.indices) {
                val posX = mClipBounds.left + i * mWidthOffset
                var posY: Float = heightCenter.toFloat() //if (mPositionGravity == PositionGravity.TOP) mClipBounds.top.toFloat() else mClipBounds.bottom.toFloat()
                mSrcY[i] = posY
                mDestY[i] = posY
                mBezierPoints[i][posX] = posY
            }
        }

        if (isVisualizationEnabled && mRawAudioBytes != null) {
            if (mRawAudioBytes.isEmpty()) {
                return
            }
            mWavePath.rewind()

            //find the destination bezier point for a batch
            if (nBatchCount == 0) {
                val randPosY = mDestY[mRandom.nextInt(nPoints)]
                for (i in mBezierPoints.indices) {
                    val x = ceil(((i + 1) * (mRawAudioBytes.size / nPoints)).toDouble()).toInt()
                    var t = 0
                    if (x < 1024) t = heightCenter + (abs(mRawAudioBytes[x].toInt()) + 128).toByte() * heightCenter / 128
                    val posY: Float = t.toFloat()//if (mPositionGravity == PositionGravity.TOP) (mClipBounds.bottom - t).toFloat() else (mClipBounds.top + t).toFloat()

                    //change the source and destination y
                    mSrcY[i] = mDestY[i]
                    mDestY[i] = posY
                }
                mDestY[mBezierPoints.size - 1] = randPosY
            }

            //increment batch count
            nBatchCount++

            //for smoothing animation
            for (i in mBezierPoints.indices) {
                mBezierPoints[i].y =
                    mSrcY[i] + nBatchCount.toFloat() / mMaxBatchCount * (mDestY[i] - mSrcY[i])
            }

            //reset the batch count
            if (nBatchCount == mMaxBatchCount) nBatchCount = 0

            //calculate the bezier curve control points
            for (i in 1 until mBezierPoints.size) {
                mBezierControlPoints1[i][(mBezierPoints[i].x + mBezierPoints[i - 1].x) / 2] =
                    mBezierPoints[i - 1].y
                mBezierControlPoints2[i][(mBezierPoints[i].x + mBezierPoints[i - 1].x) / 2] =
                    mBezierPoints[i].y
            }

            //create the path
            mWavePath.moveTo(mBezierPoints[0].x, mBezierPoints[0].y)
            for (i in 1 until mBezierPoints.size) {
                mWavePath.cubicTo(
                    mBezierControlPoints1[i].x, mBezierControlPoints1[i].y,
                    mBezierControlPoints2[i].x, mBezierControlPoints2[i].y,
                    mBezierPoints[i].x, mBezierPoints[i].y
                )
            }

            //add last 3 line to close the view
            //mWavePath.lineTo(mClipBounds.right, mBezierPoints[0].y);
            if (mPaintStyle == PaintStyle.FILL) {
                mWavePath.lineTo(mClipBounds.right.toFloat(), mClipBounds.bottom.toFloat())
                mWavePath.lineTo(mClipBounds.left.toFloat(), mClipBounds.bottom.toFloat())
                mWavePath.close()
            }
            canvas.drawPath(mWavePath, mPaint)
        }
        super.onDraw(canvas)
    }

    companion object {
        private const val WAVE_MAX_POINTS = 54
        private const val WAVE_MIN_POINTS = 3
    }
}