package eu.bamboo.voice_animation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import eu.bamboo.voice_animation.databinding.ActivityNextBinding


class NextActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNextBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNextBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.toNewScreen.setOnClickListener {
            startActivity(Intent(this, NewActivity::class.java))
        }

        binding.waveView1.addDefaultWaves(2, 1)
        binding.waveView1.startAnimation()
    }

    override fun onResume() {
        super.onResume()
        binding.waveView1.resumeAnimation()
//        binding.waveView2.resumeAnimation()
    }

    override fun onPause() {
        super.onPause()
        binding.waveView1.pauseAnimation()
//        binding.waveView2.pauseAnimation()
    }

}