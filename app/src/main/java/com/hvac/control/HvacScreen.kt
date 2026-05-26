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
    private var fanSpeed = 1
    private var isDefogOn = false
    private var isAcOn = false
    private var ventMode = "Face"
    private val ventModes = listOf("Face", "Feet", "Both", "Defrost")

    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()

        // Temperature Row
        listBuilder.addItem(
            Row.Builder()
                .setTitle("Temperature: $temperature°C")
                .addText("Tap to increase, long press not supported") // Template limitation
                .setOnClickListener {
                    temperature++
                    if (temperature > 30) temperature = 16
                    UsbSerialManager.connect(carContext)
                    UsbSerialManager.sendCommand("SET_TEMP:$temperature")
                    invalidate()
                }
                .build()
        )

        // Fan Speed Row
        listBuilder.addItem(
            Row.Builder()
                .setTitle("Fan Speed: $fanSpeed")
                .setOnClickListener {
                    fanSpeed = (fanSpeed + 1) % 5
                    UsbSerialManager.connect(carContext)
                    UsbSerialManager.sendCommand("SET_FAN:$fanSpeed")
                    invalidate()
                }
                .build()
        )

        // Vent Mode Row
        listBuilder.addItem(
            Row.Builder()
                .setTitle("Vent Mode: $ventMode")
                .setOnClickListener {
                    val currentIndex = ventModes.indexOf(ventMode)
                    ventMode = ventModes[(currentIndex + 1) % ventModes.size]
                    UsbSerialManager.connect(carContext)
                    UsbSerialManager.sendCommand("SET_VENT:$ventMode")
                    invalidate()
                }
                .build()
        )

        // Defog Row
        listBuilder.addItem(
            Row.Builder()
                .setTitle("Defogger: ${if (isDefogOn) "ON" else "OFF"}")
                .setOnClickListener {
                    isDefogOn = !isDefogOn
                    UsbSerialManager.connect(carContext)
                    UsbSerialManager.sendCommand("SET_DEFOG:${if (isDefogOn) 1 else 0}")
                    invalidate()
                }
                .build()
        )

        // A/C Row
        listBuilder.addItem(
            Row.Builder()
                .setTitle("A/C: ${if (isAcOn) "ON" else "OFF"}")
                .setOnClickListener {
                    isAcOn = !isAcOn
                    UsbSerialManager.connect(carContext)
                    UsbSerialManager.sendCommand("SET_AC:${if (isAcOn) 1 else 0}")
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
