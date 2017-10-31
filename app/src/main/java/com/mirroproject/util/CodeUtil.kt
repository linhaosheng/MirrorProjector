package com.mirroproject.util

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.util.Log
import java.util.*

/**
 * Created by reeman on 2017/10/31.
 */
class CodeUtil {
    companion object {
        fun getVersion(context: Context): String {
            try {
                val manager = context.packageManager
                val info = manager.getPackageInfo(context.packageName, 0)
                return info.versionName
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }

        }

        fun getBlueToothCode(): String {
            var address = ""
            try {
                val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                val address1 = bluetoothAdapter.address
                if (address1 != null && address1 != "") {
                    address = address1.replace(":", "").trim { it <= ' ' }
                }
            } catch (e: Exception) {
                Log.e("catch", "====或去蓝牙地址异常 =" + e.toString())
            }

            return address
        }

        fun getDeviceID(): String {
            val data = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
            val random = Random()
            val idBuilder = StringBuilder()
            for (i in 0 until data.size - 1) {
                val index = random.nextInt(data.size)
                idBuilder.append(data[index])
                if (idBuilder.toString().length > 3) {
                    break
                }
            }
            return idBuilder.toString()
        }
    }
}