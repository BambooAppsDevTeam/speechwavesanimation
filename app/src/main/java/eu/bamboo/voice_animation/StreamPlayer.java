package eu.bamboo.voice_animation;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Exposes the ability to play raw audio data from an InputStream.
 */
public final class StreamPlayer {
    private final String TAG = "StreamPlayer";
    // default sample rate for .wav from Watson TTS
    // see https://console.bluemix.net/docs/services/text-to-speech/http.html#format
    public final int DEFAULT_SAMPLE_RATE = 22050;

    private AudioTrack audioTrack;

    private static byte[] convertStreamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[10240];
        int i;
        while ((i = is.read(buff, 0, buff.length)) > 0) {
            baos.write(buff, 0, i);
        }

        return baos.toByteArray();
    }

    /**
     * Play the given InputStream. The stream must be a PCM audio format with a sample rate of 22050.
     *
     * @param stream the stream derived from a PCM audio source
     */
    public void playStream(InputStream stream, Handler handler) {
        try {
            //FIXME you can read stream only once.
            //FIXME check header
//            byte[] data = convertStreamToByteArray(stream);
//            int headSize = 44, metaDataSize = 48;
//            int destPos = headSize + metaDataSize;
//            int rawLength = data.length - destPos;
//            byte[] d = new byte[rawLength];
//            System.arraycopy(data, destPos, d, 0, rawLength);
            play();
//            byte[] buff = new byte[1024];
            int i = 0;
            Log.w(TAG, "start data...");
            byte[] buff = new byte[1024];
            while (true) {
                int step = stream.read(buff, 0, buff.length);
                if (step <= 0) break;
                Message m = new Message();
                Bundle b = new Bundle();
                b.putByteArray("bytes", buff);
                m.setData(b);
                audioTrack.write(buff, 0, step);
                handler.sendMessage(m);
            } //FIXME recheck header for start noise
            Log.w(TAG, "end data...");

//      audioTrack.write(d, 0, d.length);
            stream.close();
            if (audioTrack != null && audioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
                audioTrack.release();
            }
        } catch (IOException e2) {
            Log.e(TAG, e2.getMessage());
        }
    }

    public void playStream(InputStream stream) {
        try {
            byte[] data = convertStreamToByteArray(stream);
            int headSize = 44, metaDataSize = 48;
            int destPos = headSize + metaDataSize;
            int rawLength = data.length - destPos;
            byte[] d = new byte[rawLength];
            System.arraycopy(data, destPos, d, 0, rawLength);
            play();
            Log.w(TAG, "start data...");
            audioTrack.write(d, 0, d.length);
            stream.close();
            if (audioTrack != null && audioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
                audioTrack.release();
            }
        } catch (IOException e2) {
            Log.e(TAG, e2.getMessage());
        }
    }

    /**
     * Play the given InputStream. The stream must be a PCM audio format.
     *
     * @param stream     the stream derived from a PCM audio source
     * @param sampleRate the sample rate for the provided stream
     */
    public void playStream(InputStream stream, int sampleRate) {
        try {
            byte[] data = convertStreamToByteArray(stream);
            int headSize = 44, metaDataSize = 48;
            int destPos = headSize + metaDataSize;
            int rawLength = data.length - destPos;
            byte[] d = new byte[rawLength];
            System.arraycopy(data, destPos, d, 0, rawLength);
            initPlayer(sampleRate);
            audioTrack.write(d, 0, d.length);
            stream.close();
            if (audioTrack != null && audioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
                audioTrack.release();
            }
        } catch (IOException e2) {
            Log.e(TAG, e2.getMessage());
        }
    }

    /**
     * Interrupt the audioStream.
     */
    public void interrupt() {
        if (audioTrack != null) {
            if (audioTrack.getState() == AudioTrack.STATE_INITIALIZED
                    || audioTrack.getState() == AudioTrack.PLAYSTATE_PLAYING) {
                audioTrack.pause();
            }
            audioTrack.flush();
            audioTrack.release();
        }
    }

    /**
     * Initialize AudioTrack by getting buffersize
     *
     * @param sampleRate the sample rate for the audio to be played
     */
    public void initPlayer(int sampleRate) {
        synchronized (this) {
            int bufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize == AudioTrack.ERROR_BAD_VALUE) {
                throw new RuntimeException("Could not determine buffer size for audio");
            }

            audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize,
                    AudioTrack.MODE_STREAM
            );
//      audioTrack.play();
        }
    }

    public void play() {
        audioTrack.play();
    }

    public int getSessionId() {
        return audioTrack.getAudioSessionId();
    }
}
