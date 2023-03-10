package eu.bamboo.voice_animation

import android.Manifest
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import eu.bamboo.speech_waves_animation.VoiceVisualizer
import eu.bamboo.voice_animation.databinding.ActivityLineVoiceBinding

class LineVoiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLineVoiceBinding

    private lateinit var mediaPlayer: MediaPlayer

    private var visualizer: Visualizer? = null

    private val neededPermissionsArray = listOfNotNull(
        Manifest.permission.RECORD_AUDIO
    ).toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLineVoiceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mediaPlayer = MediaPlayer.create(this, R.raw.voice_main_feature_5)
    }

    override fun onStart() {
        super.onStart()

        startMediaPlayerIfPermitted()
    }

    override fun onStop() {
        mediaPlayer.stop()
        mediaPlayer.setOnCompletionListener(null)
        visualizer?.release()

        super.onStop()
    }

    private fun setAudioSessionId(audioSessionId: Int) {
        visualizer?.release()

        val visualizer = object : VoiceVisualizer(audioSessionId) {
            override fun onWaveUpdates(bytes: ByteArray) {
                binding.musicLine.updateVisualizer(bytes)
            }
        }
        this.visualizer = visualizer
    }

    private fun startMediaPlayerIfPermitted() {
        if (appHasAudioRecordPermissions()) {
            startMediaPlayer()
        } else {
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { resultMap ->
                neededPermissionsArray
                    .all { resultMap[it] ?: false }
                    .then { startMediaPlayer() }
            }.launch(neededPermissionsArray)
        }
    }

    private fun appHasAudioRecordPermissions() = neededPermissionsArray.all { hasPermission(it) }

    private fun startMediaPlayer() {
        mediaPlayer.start()

        val id = mediaPlayer.audioSessionId
        if (id != -1) {
            setAudioSessionId(id)
        }

        mediaPlayer.setOnCompletionListener {
            mediaPlayer.start()
        }
    }

}