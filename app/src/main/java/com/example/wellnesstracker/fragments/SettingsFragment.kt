package com.example.wellnesstracker.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.wellnesstracker.R
import com.example.wellnesstracker.utils.DataManager
import com.example.wellnesstracker.utils.NotificationHelper

class SettingsFragment : Fragment() {

    private lateinit var dataManager: DataManager
    private lateinit var notificationHelper: NotificationHelper

    private lateinit var switchHydration: Switch
    private lateinit var etInterval: EditText
    private lateinit var btnSaveSettings: Button

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(requireContext(), "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show()
            switchHydration.isChecked = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataManager = DataManager(requireContext())
        notificationHelper = NotificationHelper(requireContext())

        switchHydration = view.findViewById(R.id.switchHydration)
        etInterval = view.findViewById(R.id.etInterval)
        btnSaveSettings = view.findViewById(R.id.btnSaveSettings)

        loadSettings()

        btnSaveSettings.setOnClickListener {
            saveSettings()
        }

        switchHydration.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkNotificationPermission()
            }
        }
    }

    private fun loadSettings() {
        switchHydration.isChecked = dataManager.isHydrationEnabled()
        etInterval.setText(dataManager.getHydrationInterval().toString())
    }

    private fun saveSettings() {
        val intervalStr = etInterval.text.toString().trim()

        if (intervalStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter interval", Toast.LENGTH_SHORT).show()
            return
        }

        val interval = intervalStr.toIntOrNull()
        if (interval == null || interval < 5) {
            Toast.makeText(requireContext(), "Interval must be at least 5 minutes", Toast.LENGTH_SHORT).show()
            return
        }

        dataManager.saveHydrationEnabled(switchHydration.isChecked)
        dataManager.saveHydrationInterval(interval)

        if (switchHydration.isChecked) {
            notificationHelper.scheduleHydrationReminder(interval)
            Toast.makeText(requireContext(), "Hydration reminders enabled", Toast.LENGTH_SHORT).show()
        } else {
            notificationHelper.cancelHydrationReminder()
            Toast.makeText(requireContext(), "Hydration reminders disabled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission granted
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Toast.makeText(
                        requireContext(),
                        "Notification permission is required for hydration reminders",
                        Toast.LENGTH_LONG
                    ).show()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}