package com.example.wellnesstracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wellnesstracker.R
import com.example.wellnesstracker.models.MoodEntry
import com.example.wellnesstracker.utils.DataManager

/**
 * RecyclerView Adapter for displaying mood entries
 */
class MoodAdapter(
    private var moodEntries: MutableList<MoodEntry>,
    private val dataManager: DataManager,
    private val onDelete: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    inner class MoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val moodEmoji: TextView = view.findViewById(R.id.moodEmoji)
        val moodDate: TextView = view.findViewById(R.id.moodDate)
        val moodTime: TextView = view.findViewById(R.id.moodTime)
        val moodNote: TextView = view.findViewById(R.id.moodNote)
        val btnDelete: Button = view.findViewById(R.id.btnDeleteMood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood, parent, false)
        return MoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val entry = moodEntries[position]

        holder.moodEmoji.text = entry.emoji
        holder.moodDate.text = dataManager.formatDate(entry.timestamp)
        holder.moodTime.text = dataManager.formatTime(entry.timestamp)

        if (entry.note.isNotEmpty()) {
            holder.moodNote.text = entry.note
            holder.moodNote.visibility = View.VISIBLE
        } else {
            holder.moodNote.visibility = View.GONE
        }

        holder.btnDelete.setOnClickListener { onDelete(entry) }
    }

    override fun getItemCount(): Int = moodEntries.size

    fun updateMoodEntries(newEntries: List<MoodEntry>) {
        moodEntries.clear()
        moodEntries.addAll(newEntries)
        notifyDataSetChanged()
    }
}