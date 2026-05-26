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
                UsbSerialManager.connect(context) { success ->
                    if (success) UsbSerialManager.sendCommand("TEMP_UP")
                }
            }
            "ACTION_TEMP_DOWN" -> {
                UsbSerialManager.connect(context) { success ->
                    if (success) UsbSerialManager.sendCommand("TEMP_DOWN")
                }
            }
            "ACTION_FAN_CYCLE" -> {
                UsbSerialManager.connect(context) { success ->
                    if (success) UsbSerialManager.sendCommand("FAN_CYCLE")
                }
            }
            "ACTION_DEFOG_TOGGLE" -> {
                UsbSerialManager.connect(context) { success ->
                    if (success) UsbSerialManager.sendCommand("DEFOG_TOGGLE")
                }
            }
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.hvac_widget)

        val upIntent = Intent(context, HvacWidgetProvider::class.java).apply { action = "ACTION_TEMP_UP" }
        views.setOnClickPendingIntent(R.id.btn_up, PendingIntent.getBroadcast(context, 0, upIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))

        val downIntent = Intent(context, HvacWidgetProvider::class.java).apply { action = "ACTION_TEMP_DOWN" }
        views.setOnClickPendingIntent(R.id.btn_down, PendingIntent.getBroadcast(context, 1, downIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))

        val fanIntent = Intent(context, HvacWidgetProvider::class.java).apply { action = "ACTION_FAN_CYCLE" }
        views.setOnClickPendingIntent(R.id.btn_fan, PendingIntent.getBroadcast(context, 2, fanIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))

        val defogIntent = Intent(context, HvacWidgetProvider::class.java).apply { action = "ACTION_DEFOG_TOGGLE" }
        views.setOnClickPendingIntent(R.id.btn_defog, PendingIntent.getBroadcast(context, 3, defogIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
