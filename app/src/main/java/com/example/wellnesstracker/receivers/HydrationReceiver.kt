package com.example.wellnesstracker.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.wellnesstracker.utils.DataManager
import com.example.wellnesstracker.utils.NotificationHelper

/**
 * BroadcastReceiver to handle hydration reminder alarms
 */
class HydrationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val dataManager = DataManager(context)

        if (dataManager.isHydrationEnabled()) {
            val notificationHelper = NotificationHelper(context)
            notificationHelper.showHydrationNotification()
        }

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            if (dataManager.isHydrationEnabled()) {
                val interval = dataManager.getHydrationInterval()
                val notificationHelper = NotificationHelper(context)
                notificationHelper.scheduleHydrationReminder(interval)
            }
        }
    }
}