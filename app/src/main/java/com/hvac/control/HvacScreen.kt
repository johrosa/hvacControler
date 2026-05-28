package com.hvac.control

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.model.Toggle
import androidx.core.graphics.drawable.IconCompat

class HvacScreen(carContext: CarContext) : Screen(carContext) {
    private var temperature = 22
    private var fanSpeed = 1
    private var isDefogOn = false
    private var isAcOn = false
    private var ventMode = "Face"
    private val ventModes = listOf("Face", "Feet", "Both", "Defrost")

    private fun createCarIcon(resId: Int, tint: CarColor): CarIcon {
        return CarIcon.Builder(IconCompat.createWithResource(carContext, resId))
            .setTint(tint)
            .build()
    }

    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()

        // Temperature Up Row
        listBuilder.addItem(
            Row.Builder()
                .setTitle("Increase Temperature")
                .addText("Current: $temperature°C")
                .setImage(createCarIcon(R.drawable.ic_temp_up, CarColor.RED))
                .setOnClickListener {
                    if (temperature < 30) {
                        temperature++
                        UsbSerialManager.connect(carContext)
                        UsbSerialManager.sendCommand("SET_TEMP:$temperature")
                        invalidate()
                    }
                }
                .build()
        )

        // Temperature Down Row
        listBuilder.addItem(
            Row.Builder()
                .setTitle("Decrease Temperature")
                .addText("Current: $temperature°C")
                .setImage(createCarIcon(R.drawable.ic_temp_down, CarColor.BLUE))
                .setOnClickListener {
                    if (temperature > 16) {
                        temperature--
                        UsbSerialManager.connect(carContext)
                        UsbSerialManager.sendCommand("SET_TEMP:$temperature")
                        invalidate()
                    }
                }
                .build()
        )

        // Fan Speed Row
        listBuilder.addItem(
            Row.Builder()
                .setTitle("Fan Speed: $fanSpeed")
                .setImage(createCarIcon(R.drawable.ic_fan, CarColor.GREEN))
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
                .setImage(createCarIcon(R.drawable.ic_vent, CarColor.YELLOW))
                .setOnClickListener {
                    val currentIndex = ventModes.indexOf(ventMode)
                    ventMode = ventModes[(currentIndex + 1) % ventModes.size]
                    UsbSerialManager.connect(carContext)
                    UsbSerialManager.sendCommand("SET_VENT:$ventMode")
                    invalidate()
                }
                .build()
        )

        // Defog Toggle
        listBuilder.addItem(
            Row.Builder()
                .setTitle("Defogger")
                .setImage(createCarIcon(R.drawable.ic_defrost, CarColor.YELLOW))
                .setToggle(
                    Toggle.Builder { checked ->
                        isDefogOn = checked
                        UsbSerialManager.connect(carContext)
                        UsbSerialManager.sendCommand("SET_DEFOG:${if (isDefogOn) 1 else 0}")
                        invalidate()
                    }
                    .setChecked(isDefogOn)
                    .build()
                )
                .build()
        )

        // A/C Toggle
        listBuilder.addItem(
            Row.Builder()
                .setTitle("A/C")
                .setImage(createCarIcon(R.drawable.ic_ac, CarColor.BLUE))
                .setToggle(
                    Toggle.Builder { checked ->
                        isAcOn = checked
                        UsbSerialManager.connect(carContext)
                        UsbSerialManager.sendCommand("SET_AC:${if (isAcOn) 1 else 0}")
                        invalidate()
                    }
                    .setChecked(isAcOn)
                    .build()
                )
                .build()
        )

        return ListTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle("HVAC Control")
            .setHeaderAction(Action.APP_ICON)
            .build()
    }
}
