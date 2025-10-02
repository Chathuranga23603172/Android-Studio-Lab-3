package com.example.wellnesstracker.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wellnesstracker.R
import com.example.wellnesstracker.adapters.HabitAdapter
import com.example.wellnesstracker.models.Habit
import com.example.wellnesstracker.utils.DataManager
import com.example.wellnesstracker.widgets.HabitWidgetProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

/**
 * Fragment for managing daily wellness habits
 */
class HabitsFragment : Fragment() {

    private lateinit var dataManager: DataManager
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddHabit: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_habits, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataManager = DataManager(requireContext())

        recyclerView = view.findViewById(R.id.recyclerViewHabits)
        fabAddHabit = view.findViewById(R.id.fabAddHabit)

        setupRecyclerView()

        fabAddHabit.setOnClickListener {
            showAddHabitDialog()
        }

        loadHabits()
    }

    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter(
            mutableListOf(),
            onIncrement = { habit -> incrementHabit(habit) },
            onDecrement = { habit -> decrementHabit(habit) },
            onEdit = { habit -> showEditHabitDialog(habit) },
            onDelete = { habit -> showDeleteHabitDialog(habit) }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = habitAdapter
        }
    }

    private fun loadHabits() {
        val habits = dataManager.loadHabits()
        habitAdapter.updateHabits(habits)
        updateWidget()
    }

    private fun showAddHabitDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_habit, null)

        val etHabitName = dialogView.findViewById<EditText>(R.id.etHabitName)
        val etTargetCount = dialogView.findViewById<EditText>(R.id.etTargetCount)

        AlertDialog.Builder(requireContext())
            .setTitle("Add New Habit")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etHabitName.text.toString().trim()
                val targetStr = etTargetCount.text.toString().trim()

                if (name.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter habit name", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (targetStr.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter target count", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val target = targetStr.toIntOrNull()
                if (target == null || target <= 0) {
                    Toast.makeText(requireContext(), "Please enter a valid target count", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val habit = Habit(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    targetCount = target,
                    currentCount = 0,
                    lastUpdatedDate = dataManager.getCurrentDate()
                )

                dataManager.addHabit(habit)
                loadHabits()
                Toast.makeText(requireContext(), "Habit added!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditHabitDialog(habit: Habit) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_habit, null)

        val etHabitName = dialogView.findViewById<EditText>(R.id.etHabitName)
        val etTargetCount = dialogView.findViewById<EditText>(R.id.etTargetCount)

        etHabitName.setText(habit.name)
        etTargetCount.setText(habit.targetCount.toString())

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Habit")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = etHabitName.text.toString().trim()
                val targetStr = etTargetCount.text.toString().trim()

                if (name.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter habit name", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (targetStr.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter target count", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val target = targetStr.toIntOrNull()
                if (target == null || target <= 0) {
                    Toast.makeText(requireContext(), "Please enter a valid target count", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                habit.name = name
                habit.targetCount = target
                dataManager.updateHabit(habit)
                loadHabits()
                Toast.makeText(requireContext(), "Habit updated!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteHabitDialog(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete '${habit.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                dataManager.deleteHabit(habit.id)
                loadHabits()
                Toast.makeText(requireContext(), "Habit deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun incrementHabit(habit: Habit) {
        if (habit.currentCount < habit.targetCount) {
            habit.currentCount++
            habit.lastUpdatedDate = dataManager.getCurrentDate()
            dataManager.updateHabit(habit)
            loadHabits()
        }
    }

    private fun decrementHabit(habit: Habit) {
        if (habit.currentCount > 0) {
            habit.currentCount--
            habit.lastUpdatedDate = dataManager.getCurrentDate()
            dataManager.updateHabit(habit)
            loadHabits()
        }
    }

    private fun updateWidget() {
        HabitWidgetProvider.updateAllWidgets(requireContext())
    }
}