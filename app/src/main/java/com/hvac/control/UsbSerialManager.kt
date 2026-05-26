package com.hvac.control

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import java.io.IOException
import java.util.concurrent.Executors

object UsbSerialManager {
    private var port: UsbSerialPort? = null
    private val executor = Executors.newSingleThreadExecutor()
    private const val ACTION_USB_PERMISSION = "com.hvac.control.USB_PERMISSION"

    fun connect(context: Context, onResult: (Boolean) -> Unit = {}) {
        executor.execute {
            if (port != null) {
                onResult(true)
                return@execute
            }

            val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
            if (availableDrivers.isEmpty()) {
                onResult(false)
                return@execute
            }

            val driver = availableDrivers[0]
            val device = driver.device

            if (!manager.hasPermission(device)) {
                val permissionIntent = PendingIntent.getBroadcast(
                    context, 0, Intent(ACTION_USB_PERMISSION),
                    PendingIntent.FLAG_IMMUTABLE
                )
                manager.requestPermission(device, permissionIntent)
                onResult(false)
                return@execute
            }

            val connection: UsbDeviceConnection? = manager.openDevice(device)
            if (connection == null) {
                onResult(false)
                return@execute
            }

            val port = driver.ports[0]
            try {
                port.open(connection)
                port.setParameters(115200, 8, UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1)
                this.port = port
                onResult(true)
            } catch (e: IOException) {
                onResult(false)
            }
        }
    }

    fun sendCommand(command: String, onResult: (Boolean) -> Unit = {}) {
        executor.execute {
            val port = this.port
            if (port == null) {
                onResult(false)
                return@execute
            }
            try {
                port.write(command.toByteArray(), 1000)
                onResult(true)
            } catch (e: IOException) {
                onResult(false)
            }
        }
    }

    fun disconnect() {
        executor.execute {
            try {
                port?.close()
            } catch (e: IOException) {
                // Ignore
            }
            port = null
        }
    }
}
