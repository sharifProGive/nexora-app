package com.example.update.repository

object SemVerUtil {
    /**
     * Compares two semantic version strings (e.g., "1.1.0" and "1.0.0").
     * Returns:
     *   > 0 if v1 > v2
     *   < 0 if v1 < v2
     *   0 if v1 == v2
     */
    fun compare(v1: String, v2: String): Int {
        val parts1 = v1.trim().removePrefix("v").split(".").mapNotNull { it.toIntOrNull() }
        val parts2 = v2.trim().removePrefix("v").split(".").mapNotNull { it.toIntOrNull() }

        val maxLen = maxOf(parts1.size, parts2.size)
        for (i in 0 until maxLen) {
            val p1 = parts1.getOrElse(i) { 0 }
            val p2 = parts2.getOrElse(i) { 0 }
            if (p1 != p2) {
                return p1.compareTo(p2)
            }
        }
        return 0
    }
}
