package eu.bamboo.voice_animation

import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import eu.bamboo.speech_waves_animation.VoiceVisualizer
import eu.bamboo.voice_animation.databinding.FragmentLineVoiceBinding

class LineVoiceFragment : Fragment(R.layout.fragment_line_voice) {

    private var _binding: FragmentLineVoiceBinding? = null
    private val binding get() = _binding!!

    private lateinit var mediaPlayer: MediaPlayer

    private var visualizer: Visualizer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLineVoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaPlayer = MediaPlayer.create(context, R.raw.audio)
    }

    override fun onStart() {
        super.onStart()

        startMediaPlayer()
    }

    override fun onStop() {
        mediaPlayer.stop()
        mediaPlayer.setOnCompletionListener(null)
        visualizer?.release()

        super.onStop()
    }

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

    private fun setAudioSessionId(audioSessionId: Int) {
        visualizer?.release()

        val visualizer = object : VoiceVisualizer(audioSessionId) {
            override fun onWaveUpdates(bytes: ByteArray) {
                binding.musicLine.updateVisualizer(bytes)
            }
        }
        this.visualizer = visualizer
    }

}