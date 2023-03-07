package eu.bamboo.speech_waves_animation

import android.widget.SeekBar

public abstract class OnSeekBarChangeListener: SeekBar.OnSeekBarChangeListener {

    override fun onStartTrackingTouch(seekBar: SeekBar) { }

    override fun onStopTrackingTouch(seekBar: SeekBar) { }
}