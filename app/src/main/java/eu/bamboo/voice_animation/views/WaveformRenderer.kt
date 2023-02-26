package eu.bamboo.voice_animation.views

import android.graphics.Canvas

interface WaveformRenderer {
    fun render(canvas: Canvas?, waveform: ByteArray?)
}