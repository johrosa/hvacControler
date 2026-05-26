package com.hvac.control

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat

class HvacScreen(carContext: CarContext) : Screen(carContext) {
    private var temperature = 22

    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()

        listBuilder.addItem(
            Row.Builder()
                .setTitle("Temperature: $temperature°C")
                .addText("Control your vehicle's climate")
                .build()
        )

        listBuilder.addItem(
            Row.Builder()
                .setTitle("Increase Temperature")
                .setOnClickListener {
                    temperature++
                    UsbSerialManager.connect(carContext)
                    UsbSerialManager.sendCommand("TEMP_UP")
                    invalidate()
                }
                .build()
        )

        listBuilder.addItem(
            Row.Builder()
                .setTitle("Decrease Temperature")
                .setOnClickListener {
                    temperature--
                    UsbSerialManager.connect(carContext)
                    UsbSerialManager.sendCommand("TEMP_DOWN")
                    invalidate()
                }
                .build()
        )

        return ListTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle("HVAC Control")
            .setHeaderAction(Action.APP_ICON)
            .build()
    }
}
