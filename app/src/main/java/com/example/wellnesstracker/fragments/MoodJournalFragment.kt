package com.example.wellnesstracker.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wellnesstracker.R
import com.example.wellnesstracker.adapters.MoodAdapter
import com.example.wellnesstracker.models.MoodEntry
import com.example.wellnesstracker.utils.DataManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MoodJournalFragment : Fragment() {

    private lateinit var dataManager: DataManager
    private lateinit var moodAdapter: MoodAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddMood: FloatingActionButton
    private lateinit var btnShareMood: Button
    private lateinit var btnToggleChart: Button
    private lateinit var moodChart: LineChart
    private var isChartVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood_journal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataManager = DataManager(requireContext())

        recyclerView = view.findViewById(R.id.recyclerViewMoods)
        fabAddMood = view.findViewById(R.id.fabAddMood)
        btnShareMood = view.findViewById(R.id.btnShareMood)
        btnToggleChart = view.findViewById(R.id.btnToggleChart)
        moodChart = view.findViewById(R.id.moodChart)

        setupRecyclerView()

        fabAddMood.setOnClickListener {
            showAddMoodDialog()
        }

        btnShareMood.setOnClickListener {
            shareMoodSummary()
        }

        btnToggleChart.setOnClickListener {
            toggleChart()
        }

        loadMoodEntries()
        setupChart()
    }

    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter(
            mutableListOf(),
            dataManager,
            onDelete = { entry -> showDeleteMoodDialog(entry) }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moodAdapter
        }
    }

    private fun loadMoodEntries() {
        val entries = dataManager.loadMoodEntries()
        moodAdapter.updateMoodEntries(entries)
        updateChart()
    }

    private fun showAddMoodDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_mood, null)

        val emojiButtons = listOf(
            dialogView.findViewById<Button>(R.id.btnEmojiVeryHappy),
            dialogView.findViewById<Button>(R.id.btnEmojiHappy),
            dialogView.findViewById<Button>(R.id.btnEmojiNeutral),
            dialogView.findViewById<Button>(R.id.btnEmojiSad),
            dialogView.findViewById<Button>(R.id.btnEmojiVerySad)
        )

        val etNote = dialogView.findViewById<EditText>(R.id.etMoodNote)
        val tvSelectedEmoji = dialogView.findViewById<TextView>(R.id.tvSelectedEmoji)

        var selectedEmoji = ""

        emojiButtons.forEach { button ->
            button.setOnClickListener {
                selectedEmoji = button.text.toString()
                tvSelectedEmoji.text = "Selected: $selectedEmoji"

                emojiButtons.forEach { it.alpha = 0.5f }
                button.alpha = 1.0f
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("How are you feeling?")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                if (selectedEmoji.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select a mood emoji", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val note = etNote.text.toString().trim()

                val entry = MoodEntry(
                    id = UUID.randomUUID().toString(),
                    emoji = selectedEmoji,
                    note = note,
                    timestamp = System.currentTimeMillis(),
                    date = dataManager.getCurrentDate()
                )

                dataManager.addMoodEntry(entry)
                loadMoodEntries()
                Toast.makeText(requireContext(), "Mood logged!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteMoodDialog(entry: MoodEntry) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Mood Entry")
            .setMessage("Are you sure you want to delete this mood entry?")
            .setPositiveButton("Delete") { _, _ ->
                dataManager.deleteMoodEntry(entry.id)
                loadMoodEntries()
                Toast.makeText(requireContext(), "Mood entry deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun shareMoodSummary() {
        val entries = dataManager.loadMoodEntries()

        if (entries.isEmpty()) {
            Toast.makeText(requireContext(), "No mood entries to share", Toast.LENGTH_SHORT).show()
            return
        }

        val summary = buildString {
            append("My Mood Summary\n\n")
            append("Total Entries: ${entries.size}\n\n")

            append("Recent Moods:\n")
            entries.take(5).forEach { entry ->
                append("${entry.emoji} - ${dataManager.formatDate(entry.timestamp)} at ${dataManager.formatTime(entry.timestamp)}\n")
                if (entry.note.isNotEmpty()) {
                    append("  Note: ${entry.note}\n")
                }
            }

            append("\n\nGenerated by Wellness Tracker App")
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "My Mood Summary")
            putExtra(Intent.EXTRA_TEXT, summary)
        }

        startActivity(Intent.createChooser(shareIntent, "Share Mood Summary"))
    }

    private fun toggleChart() {
        isChartVisible = !isChartVisible

        if (isChartVisible) {
            moodChart.visibility = View.VISIBLE
            btnToggleChart.text = "Hide Chart"
            updateChart()
        } else {
            moodChart.visibility = View.GONE
            btnToggleChart.text = "Show Mood Trend"
        }
    }

    private fun setupChart() {
        moodChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "Day ${value.toInt()}"
                    }
                }
            }

            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
                axisMaximum = 6f
                granularity = 1f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return when (value.toInt()) {
                            5 -> "😄"
                            4 -> "🙂"
                            3 -> "😐"
                            2 -> "😔"
                            1 -> "😢"
                            else -> ""
                        }
                    }
                }
            }

            axisRight.isEnabled = false
            legend.isEnabled = true
        }
    }

    private fun updateChart() {
        val entries = dataManager.getLastWeekMoodEntries()

        if (entries.isEmpty()) {
            moodChart.clear()
            return
        }

        val chartEntries = entries.mapIndexed { index, moodEntry ->
            Entry(index.toFloat() + 1, moodEntry.getMoodValue())
        }

        val dataSet = LineDataSet(chartEntries, "Mood Trend (Last 7 Days)").apply {
            color = ContextCompat.getColor(requireContext(), R.color.primary)
            setCircleColor(ContextCompat.getColor(requireContext(), R.color.primary))
            lineWidth = 2f
            circleRadius = 4f
            setDrawCircleHole(false)
            valueTextSize = 10f
            setDrawFilled(true)
            fillColor = ContextCompat.getColor(requireContext(), R.color.primary_light)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        val lineData = LineData(dataSet)
        moodChart.data = lineData
        moodChart.invalidate()
    }
}