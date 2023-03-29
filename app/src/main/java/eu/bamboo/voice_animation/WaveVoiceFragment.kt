package eu.bamboo.voice_animation

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import eu.bamboo.speech_waves_animation.toAnimationSpeed
import eu.bamboo.voice_animation.databinding.FragmentWaveVoiceBinding

class WaveVoiceFragment : Fragment(R.layout.fragment_wave_voice) {

    private var _binding: FragmentWaveVoiceBinding? = null
    private val binding get() = _binding!!

    private lateinit var mediaPlayer: MediaPlayer
    private val player = StreamPlayer()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWaveVoiceBinding.inflate(inflater, container, false)
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

//        Thread(Runnable {
//            startMediaPlayer()
//        }).start()

        Thread {
            val audioTrackPlayer = AudioTrackPlayer(requireContext())
            audioTrackPlayer.play()
        }.start()
    }

    override fun onStop() {
        mediaPlayer.stop()
        mediaPlayer.setOnCompletionListener(null)
        player.interrupt()

        super.onStop()
    }

    val DEFAULT_SAMPLE_RATE = 22050

    private fun startMediaPlayer() {
        player.initPlayer(DEFAULT_SAMPLE_RATE)
//        player.playStream(assets.open("welcome_text_with_wave_format"),handler)
    }

    val handler = Handler(Looper.myLooper()!!,object: Handler.Callback{
        override fun handleMessage(p0: Message): Boolean {
            binding.musicWave.updateVisualizer(p0.data.getByteArray("bytes"))
            return true;
        }
    })

//    private fun setAudioSessionId2(id: Int) {
//        val audioAttributes = AudioAttributes.Builder()
//                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                .setUsage(AudioAttributes.USAGE_MEDIA)
//                .setLegacyStreamType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                .build()
//        val audioFormat = AudioFormat.Builder()
//            .setSampleRate(44100)
//            .build()
//        val audioTrack = AudioTrack.Builder()
//            .setAudioAttributes(audioAttributes)
//            .setAudioFormat(audioFormat)
//            .setSessionId(id)
//            .setBufferSizeInBytes(1024)
//            .build()
//        audioTrack
//    }

    private fun setBarListeners() {
        binding.seekBarDensity.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val density = progress.min(1) / 100f
                binding.titleDensity.text = "Change Density: $density"
                binding.musicWave.density = density
            }
        })

        binding.seekBarSpeed.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val speed = progress.toAnimationSpeed()
                binding.titleSpeed.text = "Change Speed: $speed"
                binding.musicWave.speed = speed
            }
        })

        binding.seekBarLineCount.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val lineCount = progress + 1
                binding.titleLineCount.text = "Change Line Count: $lineCount"
                binding.musicWave.pathCount = lineCount
            }
        })

        binding.seekBarPadding.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val padding = progress / 100f / 2
                binding.titlePadding.text = "Change Padding: $padding"
                binding.musicWave.windowPadding = padding
            }
        })

        binding.seekBarThickness.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val thickness = progress + 1f
                binding.titleThickness.text = "Thickness of Main Line: $thickness"
                binding.musicWave.wavePaintConfig.thickness = thickness
            }
        })

        binding.seekBarMiddleThickness.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val thickness = progress + 1f
                binding.titleMiddleThickness.text = "Thickness of Middle Line: $thickness"
                binding.musicWave.wavePaintConfig.thicknessMiddle = thickness
            }
        })

        binding.seekBarColorGradient.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val colorGradient = progress / 100f / 2
                binding.titleColorGradient.text = "Color Gradient Offset: $colorGradient"
                binding.musicWave.wavePaintConfig.colorGradientPositionOffset = colorGradient
            }
        })
    }

    private fun setDefault() {
        binding.seekBarDensity.progress = 20
        binding.seekBarSpeed.progress = 1
        binding.seekBarLineCount.progress = 3
        binding.seekBarPadding.progress = 48
        binding.seekBarThickness.progress = 5
        binding.seekBarMiddleThickness.progress = 2
        binding.seekBarColorGradient.progress = 20
        binding.colorStart.setText("#0000ff")
        binding.colorEnd.setText("#ffff00")
        binding.colorMiddle.setText("#ff0000")
        applyColors()
    }

    private fun applyColors() {
        val startColor = binding.colorStart.toColorInt()
        val endColor = binding.colorEnd.toColorInt()
        val middleColor = binding.colorMiddle.toColorInt()

        startColor ?: return
        endColor ?: return
        middleColor ?: return

        binding.musicWave.wavePaintConfig.startColor = startColor
        binding.musicWave.wavePaintConfig.endColor = endColor
        binding.musicWave.wavePaintConfig.middleColor = middleColor
    }

}