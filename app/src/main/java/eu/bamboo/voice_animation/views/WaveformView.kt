package eu.bamboo.voice_animation.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class WaveformView : View {

    private var waveform: ByteArray? = null

    private var renderer: WaveformRenderer? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setRenderer(renderer: WaveformRenderer?) {
        this.renderer = renderer
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (renderer != null) {
            renderer!!.render(canvas, waveform)
        }
    }

    fun setWaveform(waveform: ByteArray) {
        this.waveform = waveform.copyOf(waveform.size)
        invalidate()
    }
}