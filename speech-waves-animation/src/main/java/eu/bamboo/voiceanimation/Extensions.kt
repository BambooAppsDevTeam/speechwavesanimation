package eu.bamboo.voiceanimation

fun <T> Array<T>.firstOrLast(index: Int): Boolean = index == 0 || index == size - 1

fun Int.toAnimationSpeed(default: AnimationSpeed = AnimationSpeed.NORMAL): AnimationSpeed {
    return eu.bamboo.voiceanimation.AnimationSpeed.values().find { it.ordinal == this } ?: default
}