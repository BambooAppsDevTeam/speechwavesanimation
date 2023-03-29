package eu.bamboo.voice_animation

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import java.io.IOException

class AudioTrackPlayer(context: Context) {
    var mContext: Context
    var minBufferSize: Int
    var audioTrack: AudioTrack? = null
    var STOPPED = false

    init {
        Log.d("------", "init")
        mContext = context
        minBufferSize = AudioTrack.getMinBufferSize(
            44100,
            AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT
        )
    }

    fun play(): Boolean {
        Log.d("------", "play")
        var i = 0
        var music: ByteArray? = null
        val inputStream = mContext.resources.openRawResource(R.raw.pcm1644m)
        audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC, 44100,
            AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
            minBufferSize, AudioTrack.MODE_STREAM
        )
        try {
            music = ByteArray(1024)
            audioTrack!!.play()
            while (inputStream.read(music).also { i = it } != -1) {
                audioTrack!!.write(music, 0, i)
                Log.d("------", "music. i = $i. music = $music")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        audioTrack!!.stop()
        audioTrack!!.release()
        return STOPPED
    }
}