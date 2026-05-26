package com.hvac.control

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session

class HvacSession : Session() {
    override fun onCreateScreen(intent: Intent): Screen {
        return HvacScreen(carContext)
    }
}
