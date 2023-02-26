package eu.bamboo.voice_animation

import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import eu.bamboo.voice_animation.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.toSecondScreen.setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }

        binding.toMyScreen.setOnClickListener {
            startActivity(Intent(this, MyActivity::class.java))
        }

        handleButtons()
        handleSeekBars()
    }

    private fun handleButtons() {
        binding.buttonStart.setOnClickListener {
            binding.waveLineView.startAnim()
            binding.waveLineView2.startAnim()
        }

        binding.buttonStop.setOnClickListener {
            binding.waveLineView.stopAnim()
            binding.waveLineView2.stopAnim()
        }
    }

    private fun handleSeekBars() {
        binding.seekBarSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding.waveLineView.setMoveSpeed(progress.toFloat())
                binding.waveLineView2.setMoveSpeed(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        binding.seekBarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding.waveLineView.setVolume(progress)
                binding.waveLineView2.setVolume(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        binding.seekBarSensibility.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding.waveLineView.setSensibility(progress)
                binding.waveLineView2.setSensibility(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

}