package com.hvac.control

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val connectButton: Button = findViewById(R.id.connect_button)
        val statusText: TextView = findViewById(R.id.status_text)

        connectButton.setOnClickListener {
            UsbSerialManager.connect(this) { success ->
                runOnUiThread {
                    statusText.text = if (success) "Connected" else "Connection Failed"
                }
            }
        }
    }
}
