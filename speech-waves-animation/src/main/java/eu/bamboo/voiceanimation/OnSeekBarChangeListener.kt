package eu.bamboo.voiceanimation

import android.widget.SeekBar

public abstract class OnSeekBarChangeListener: SeekBar.OnSeekBarChangeListener {

    override fun onStartTrackingTouch(seekBar: SeekBar) { }

    override fun onStopTrackingTouch(seekBar: SeekBar) { }
}