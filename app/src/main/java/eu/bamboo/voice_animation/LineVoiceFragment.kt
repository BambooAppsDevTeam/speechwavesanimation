package eu.bamboo.voice_animation

import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import eu.bamboo.speech_waves_animation.toAnimationSpeed
import eu.bamboo.speech_waves_animation.visualizers.VoiceVisualizer
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

        binding.applyColors.setOnClickListener {
            applyColors()
        }

        setBarListeners()
        setDefault()

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

    private fun setBarListeners() {
        binding.symmetry.setOnCheckedChangeListener { _, isChecked ->
            binding.musicLine.symmetry = isChecked
        }
        binding.seekBarLineCount.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val lineCount = progress + 1
                binding.titleLineCount.text = "Change Line Count: $lineCount"
                binding.musicLine.lineCount = lineCount
            }
        })
        binding.seekBarSpeed.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val speed = progress.toAnimationSpeed()
                binding.titleSpeed.text = "Change Speed: $speed"
                binding.musicLine.speed = speed
            }
        })
    }

    private fun setDefault() {
        binding.symmetry.isChecked = true
        binding.seekBarLineCount.progress = 7
        binding.seekBarSpeed.progress = 1
        binding.colorBase.setText("#1D4A76")
        binding.color1.setText("#4493E2")
        binding.color2.setText("#1A5B9C")
        binding.color3.setText("#ff0000")
        binding.color4.setText("#ffffff")
        binding.color5.setText("#00ff00")
        binding.color6.setText("#00ffff")
        binding.color7.setText("#ffff00")
        binding.color8.setText("#888888")
        applyColors()
    }

    private fun applyColors() {
        val colorBase = binding.colorBase.toColorInt()
        val color1 = binding.color1.toColorInt()
        val color2 = binding.color2.toColorInt()
        val color3 = binding.color3.toColorInt()
        val color4 = binding.color4.toColorInt()
        val color5 = binding.color5.toColorInt()
        val color6 = binding.color6.toColorInt()
        val color7 = binding.color7.toColorInt()
        val color8 = binding.color8.toColorInt()

        colorBase ?: return
        color1 ?: return
        color2 ?: return
        color3 ?: return
        color4 ?: return
        color5 ?: return
        color6 ?: return
        color7 ?: return
        color8 ?: return

        binding.musicLine.linePaintConfig.apply {
            baseColor = colorBase
            colorList[0] = color1
            colorList[1] = color2
            colorList[2] = color3
            colorList[3] = color4
            colorList[4] = color5
            colorList[5] = color6
            colorList[6] = color7
            colorList[7] = color8
        }
    }

}