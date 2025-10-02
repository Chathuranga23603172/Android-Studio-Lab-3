package com.example.wellnesstracker.models

import java.io.Serializable

data class Habit(
    val id: String,
    var name: String,
    var targetCount: Int,
    var currentCount: Int = 0,
    var lastUpdatedDate: String = ""
) : Serializable {

    fun getCompletionPercentage(): Int {
        return if (targetCount > 0) {
            ((currentCount.toFloat() / targetCount) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
    }

    fun isCompleted(): Boolean {
        return currentCount >= targetCount
    }
}