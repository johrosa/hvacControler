package com.hvac.control

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class HvacWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            "ACTION_TEMP_UP" -> {
                UsbSerialManager.connect(context)
                UsbSerialManager.sendCommand("TEMP_UP")
            }
            "ACTION_TEMP_DOWN" -> {
                UsbSerialManager.connect(context)
                UsbSerialManager.sendCommand("TEMP_DOWN")
            }
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.hvac_widget)

        val upIntent = Intent(context, HvacWidgetProvider::class.java).apply {
            action = "ACTION_TEMP_UP"
        }
        val upPendingIntent = PendingIntent.getBroadcast(context, 0, upIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.btn_up, upPendingIntent)

        val downIntent = Intent(context, HvacWidgetProvider::class.java).apply {
            action = "ACTION_TEMP_DOWN"
        }
        val downPendingIntent = PendingIntent.getBroadcast(context, 1, downIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.btn_down, downPendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
