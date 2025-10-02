package com.example.wellnesstracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.wellnesstracker.fragments.HabitsFragment
import com.example.wellnesstracker.fragments.MoodJournalFragment
import com.example.wellnesstracker.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Main Activity - Entry point of the app
 * Manages bottom navigation and fragment transactions
 */
class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize bottom navigation
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(HabitsFragment())
        }

        // Setup bottom navigation listener
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_habits -> {
                    loadFragment(HabitsFragment())
                    true
                }
                R.id.nav_mood -> {
                    loadFragment(MoodJournalFragment())
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Load fragment into container
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selected_item", bottomNavigation.selectedItemId)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val selectedItemId = savedInstanceState.getInt("selected_item", R.id.nav_habits)
        bottomNavigation.selectedItemId = selectedItemId
    }
}