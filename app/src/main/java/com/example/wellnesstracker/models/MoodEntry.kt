package com.example.wellnesstracker.models

import java.io.Serializable

data class MoodEntry(
    val id: String,
    val emoji: String,
    val note: String = "",
    val timestamp: Long,
    val date: String
) : Serializable {

    fun getMoodValue(): Float {
        return when (emoji) {
            "😄" -> 5f
            "🙂" -> 4f
            "😐" -> 3f
            "😔" -> 2f
            "😢" -> 1f
            else -> 3f
        }
    }
}