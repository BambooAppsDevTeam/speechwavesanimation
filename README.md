# Audio Track Animations

Simple Visualizers to imitate any audio effect.

## Usage

### Wave Animation

![wave](./raw/wave_animation_example.mov)

#### XML

```xml
<eu.bamboo.speech_waves_animation.wave.SpeechWavesView
    android:layout_width="match_parent"
    android:layout_height="70dp"
    app:waveSpeed="normal"
    app:endColor="#a9c6f4"
    app:gradientOffset="0.1"
    app:lineCount="4"
    app:lineThickness="6"
    app:middleColor="#151764"
    app:middleLineThickness="3"
    app:startColor="#656ED1"
    app:density="0.2"
    app:windowPadding="0.24" />
```

Where:

- **startColor** is the main color of the Wave.
- **endColor** is the color in phases where wave is at _y = 0_.
- **middleColor** is for lines inside.
- **lineCount** is a number of wave lines from 1 to 8.
- **lineThickness** and **middleLineThickness** change the thickness of main and middle lines.
- **density** defines how many waves on a line
- **windowPadding** determines the size of the straight line at the edges of the animation, not affected by the wave.

### Line Animation

![line](./raw/line_animation_example.mov)

#### XML

```xml
<eu.bamboo.speech_waves_animation.line.SpeechLineView
    android:layout_width="match_parent"
    android:layout_height="8dp"
    app:baseColor="#1D4A76"
    app:color1="#4493E2"
    app:color2="#1A5B9C"
    app:lineCount="2"
    app:lineSpeed="slow"
    app:symmetry="true" />
```

Where:

- **baseColor** is the backgroung color.
- [**color1**..**color8**] define the colors of each line starts from middle to the edges.
- **symmetry** is true - means that animation is symmetrical.
- **lineCount** is a number of lines from 1 to 8.

### Kotlin

The easiest way to check animation is to run it with random byte arrays:

```kotlin
        Thread {
            while (true) {
                Thread.sleep(50)
                val b = ByteArray(1024)
                Random().nextBytes(b)
                runOnUiThread { binding.animationView.updateVisualizer(b) }
            }
        }.start()
```

Where `animationView` is a **SpeechLineView** or **SpeechWaveView**.

To attach animation to audio track we can use AudioTrack and InputStream.
StreamPlayer provides `ByteArray` that is needed for the animations.

```kotlin
val stream = requireContext().resources.openRawResource(rawRes)
player.playStream(stream) { bytes ->
    binding.musicWave.update(bytes)
}
```

Also to attach animation to audio track we can use Visualizer that requires audioSessionId from MediaPlayer.
VoiceVisualizer provides `ByteArray` that is needed for the animations.

```kotlin
val id = mediaPlayer.audioSessionId
if (id != -1) {
    val visualizer = object : VoiceVisualizer(audioSessionId) {
        override fun onWaveUpdates(bytes: ByteArray) {
            binding.animationView.update(bytes)
        }
    }
}
```

#### Note

VoiceVisualizer extends android Visualizer and subscribes to changes. So it's better to keep in mind it's needed to be released.

Also it requires the permission `android.permission.RECORD_AUDIO` in manifest file and runtime (Android 6.0+).

## Sample

There is a sample app with both animations to play with params using SeekBars, CheckBoxes and EditTexts.