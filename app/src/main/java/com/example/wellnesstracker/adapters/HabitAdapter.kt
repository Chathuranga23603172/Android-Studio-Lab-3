package com.example.wellnesstracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wellnesstracker.R
import com.example.wellnesstracker.models.Habit

class HabitAdapter(
    private var habits: MutableList<Habit>,
    private val onIncrement: (Habit) -> Unit,
    private val onDecrement: (Habit) -> Unit,
    private val onEdit: (Habit) -> Unit,
    private val onDelete: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    inner class HabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val habitName: TextView = view.findViewById(R.id.habitName)
        val habitProgress: TextView = view.findViewById(R.id.habitProgress)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        val btnDecrement: Button = view.findViewById(R.id.btnDecrement)
        val btnIncrement: Button = view.findViewById(R.id.btnIncrement)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]

        holder.habitName.text = habit.name
        holder.habitProgress.text = "${habit.currentCount} / ${habit.targetCount}"
        holder.progressBar.max = habit.targetCount
        holder.progressBar.progress = habit.currentCount

        holder.btnDecrement.isEnabled = habit.currentCount > 0
        holder.btnIncrement.isEnabled = habit.currentCount < habit.targetCount

        holder.btnIncrement.setOnClickListener { onIncrement(habit) }
        holder.btnDecrement.setOnClickListener { onDecrement(habit) }
        holder.btnEdit.setOnClickListener { onEdit(habit) }
        holder.btnDelete.setOnClickListener { onDelete(habit) }
    }

    override fun getItemCount(): Int = habits.size

    fun updateHabits(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }
}