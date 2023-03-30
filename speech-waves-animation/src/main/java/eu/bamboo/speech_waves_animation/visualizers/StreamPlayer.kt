package eu.bamboo.speech_waves_animation.visualizers

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import java.io.IOException
import java.io.InputStream

typealias OnWaveUpdates = (bytes: ByteArray?) -> Unit

class StreamPlayer private constructor(private val audioTrack: AudioTrack) {

    fun playStream(stream: InputStream, onWaveUpdates: OnWaveUpdates) {
        try {
            audioTrack.play()

            Log.w(TAG, "Start playing the stream")
            val audioData = ByteArray(1024)
            while (true) {
                val step = stream.read(audioData, 0, audioData.size)
                if (step <= 0) break
                onWaveUpdates.invoke(audioData)
                audioTrack.write(audioData, 0, step)
            }
            Log.w(TAG, "End playing the stream")

            stream.close()
            if (audioTrack.state != AudioTrack.STATE_UNINITIALIZED) {
                audioTrack.release()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Can't play the stream", e)
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

        private const val DEFAULT_BUFFER_SIZE = 1024

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