package eu.bamboo.voice_animation

import android.media.audiofx.Visualizer

abstract class VoiceVisualizer(audioSessionId: Int): Visualizer(audioSessionId) {

    init {
        captureSize = getCaptureSizeRange()[1]
        setDataCaptureListener(
            object : OnDataCaptureListener {
                override fun onWaveFormDataCapture(
                    visualizer: Visualizer,
                    bytes: ByteArray,
                    samplingRate: Int
                ) {
                    onWaveUpdates(bytes)
                }

                override fun onFftDataCapture(
                    visualizer: Visualizer,
                    bytes: ByteArray,
                    samplingRate: Int
                ) { }
            },
            getMaxCaptureRate() / 2,
            true,
            false
        )
        enabled = true
    }

    abstract fun onWaveUpdates(bytes: ByteArray)
}