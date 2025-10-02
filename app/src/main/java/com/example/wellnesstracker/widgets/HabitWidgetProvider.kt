package com.example.wellnesstracker.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.example.wellnesstracker.R
import com.example.wellnesstracker.utils.DataManager

/**
 * Home screen widget showing today's habit completion percentage
 */
class HabitWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val dataManager = DataManager(context)
            val completionPercentage = dataManager.getTodayCompletionPercentage()

            val views = RemoteViews(context.packageName, R.layout.widget_habit)

            views.setTextViewText(
                R.id.widgetPercentage,
                "$completionPercentage%"
            )
            views.setTextViewText(
                R.id.widgetLabel,
                "Today's Progress"
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = android.content.ComponentName(
                context,
                HabitWidgetProvider::class.java
            )
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }
}