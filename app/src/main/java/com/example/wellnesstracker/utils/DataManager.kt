package com.example.wellnesstracker.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.wellnesstracker.models.Habit
import com.example.wellnesstracker.models.MoodEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

/**
 * Manages all data persistence using SharedPreferences
 */
class DataManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("WellnessTrackerPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_HABITS = "habits"
        private const val KEY_MOOD_ENTRIES = "mood_entries"
        private const val KEY_HYDRATION_INTERVAL = "hydration_interval"
        private const val KEY_HYDRATION_ENABLED = "hydration_enabled"
    }

    // Habit Management
    fun saveHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)
        prefs.edit().putString(KEY_HABITS, json).apply()
    }

    fun loadHabits(): MutableList<Habit> {
        val json = prefs.getString(KEY_HABITS, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Habit>>() {}.type
        val habits: MutableList<Habit> = gson.fromJson(json, type)

        val today = getCurrentDate()
        habits.forEach { habit ->
            if (habit.lastUpdatedDate != today) {
                habit.currentCount = 0
                habit.lastUpdatedDate = today
            }
        }
        saveHabits(habits)

        return habits
    }

    fun addHabit(habit: Habit) {
        val habits = loadHabits()
        habits.add(habit)
        saveHabits(habits)
    }

    fun updateHabit(updatedHabit: Habit) {
        val habits = loadHabits()
        val index = habits.indexOfFirst { it.id == updatedHabit.id }
        if (index != -1) {
            habits[index] = updatedHabit
            saveHabits(habits)
        }
    }

    fun deleteHabit(habitId: String) {
        val habits = loadHabits()
        habits.removeAll { it.id == habitId }
        saveHabits(habits)
    }

    fun getTodayCompletionPercentage(): Int {
        val habits = loadHabits()
        if (habits.isEmpty()) return 0

        val totalPercentage = habits.sumOf { it.getCompletionPercentage() }
        return totalPercentage / habits.size
    }

    // Mood Management
    fun saveMoodEntries(entries: List<MoodEntry>) {
        val json = gson.toJson(entries)
        prefs.edit().putString(KEY_MOOD_ENTRIES, json).apply()
    }

    fun loadMoodEntries(): MutableList<MoodEntry> {
        val json = prefs.getString(KEY_MOOD_ENTRIES, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<MoodEntry>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addMoodEntry(entry: MoodEntry) {
        val entries = loadMoodEntries()
        entries.add(0, entry)
        saveMoodEntries(entries)
    }

    fun deleteMoodEntry(entryId: String) {
        val entries = loadMoodEntries()
        entries.removeAll { it.id == entryId }
        saveMoodEntries(entries)
    }

    fun getLastWeekMoodEntries(): List<MoodEntry> {
        val entries = loadMoodEntries()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val weekAgo = calendar.timeInMillis

        return entries.filter { it.timestamp >= weekAgo }
            .sortedBy { it.timestamp }
    }

    // Hydration Settings
    fun saveHydrationInterval(minutes: Int) {
        prefs.edit().putInt(KEY_HYDRATION_INTERVAL, minutes).apply()
    }

    fun getHydrationInterval(): Int {
        return prefs.getInt(KEY_HYDRATION_INTERVAL, 60)
    }

    fun saveHydrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HYDRATION_ENABLED, enabled).apply()
    }

    fun isHydrationEnabled(): Boolean {
        return prefs.getBoolean(KEY_HYDRATION_ENABLED, true)
    }

    // Helper Methods
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}