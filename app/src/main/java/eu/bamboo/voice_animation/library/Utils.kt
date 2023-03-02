package eu.bamboo.voice_animation.library

fun <T> Array<T>.firstOrLast(index: Int): Boolean = index == 0 || index == size - 1

fun Int.toAnimationSpeed(default: VoiceWave.AnimationSpeed = VoiceWave.AnimationSpeed.NORMAL): VoiceWave.AnimationSpeed {
    return VoiceWave.AnimationSpeed.values().find { it.ordinal == this } ?: default
}

fun Int.min(min: Int) = if (this < min) min else this