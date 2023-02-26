package eu.bamboo.voice_animation.algorithm

class LagrangeInterpolation {

    /**
     * function to interpolate the given
     * data points using Lagrange's formula
     * `currentX` corresponds to the new data point
     * whose value is to be obtained n
     * represents the number of known data points
     */
    fun interpolate(f: Array<Point>, currentX: Int, n: Int): Double {
        var result = 0.0
        for (i in 0 until n) {
            var term: Double = f[i].y.toDouble()
            for (j in 0 until n) {
                if (j != i) term = term * (currentX - f[j].x) / (f[i].x - f[j].x)
            }
            result += term
        }
        return result
    }
}