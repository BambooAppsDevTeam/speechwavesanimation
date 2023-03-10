package eu.bamboo.voice_animation

import android.widget.SeekBar

abstract class OnSeekBarChangeListener: SeekBar.OnSeekBarChangeListener {

    override fun onStartTrackingTouch(seekBar: SeekBar) { }

    override fun onStopTrackingTouch(seekBar: SeekBar) { }
}