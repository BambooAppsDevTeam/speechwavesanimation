package eu.bamboo.voice_animation.visualizers

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import java.io.InputStream

typealias OnWaveUpdates = (bytes: ByteArray) -> Unit

class StreamPlayer private constructor(private val audioTrack: AudioTrack) {

    fun playStream(stream: InputStream, onWaveUpdates: OnWaveUpdates) {
        try {
            audioTrack.play()

            Log.d(TAG, "Start playing the stream")
            val audioData = ByteArray(DEFAULT_BUFFER_SIZE)
            var step: Int
            while (stream.read(audioData, 0, audioData.size).also { step = it } > 0) {
                onWaveUpdates.invoke(audioData.filterIndexed { index, _ -> index % 2 == 0 }.toByteArray())
                audioTrack.write(audioData, 0, step)
            }
            Log.d(TAG, "End playing the stream")
        } catch (e: IllegalStateException) {
            Log.d(TAG, "The stream was interrupted")
        } finally {
            stream.close()
            if (audioTrack.state != AudioTrack.STATE_UNINITIALIZED) {
                audioTrack.release()
            }
        }
    }

    fun interrupt() {
        if (audioTrack.state == AudioTrack.STATE_INITIALIZED
            || audioTrack.state == AudioTrack.PLAYSTATE_PLAYING
        ) {
            audioTrack.pause()
        }
        audioTrack.flush()
        audioTrack.release()
    }

    companion object {

        private const val TAG = "StreamPlayer"

        private const val DEFAULT_SAMPLE_RATE = 22050

        private const val DEFAULT_BUFFER_SIZE = 1024 * 2

        fun initPlayer(sampleRate: Int = DEFAULT_SAMPLE_RATE): StreamPlayer {
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).let { bufferSize ->
                if (bufferSize != AudioTrack.ERROR_BAD_VALUE) bufferSize
                else DEFAULT_BUFFER_SIZE
            }

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setLegacyStreamType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            val audioFormat = AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .setSampleRate(sampleRate)
                .build()
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(audioAttributes)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(bufferSize)
                .build()

            return StreamPlayer(audioTrack)
        }
    }
}