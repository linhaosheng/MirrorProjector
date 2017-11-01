package com.mirroproject.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.net.wifi.aware.WifiAwareManager
import java.net.*

/**
 * Created by reeman on 2017/10/30.
 */
class NetWorkUtils {
    companion object {

        fun isNetworkConnected(context: Context): (Boolean) {
            if (context != null) {
                val systemService = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetworkInfo = systemService.activeNetworkInfo
                if (activeNetworkInfo != null) {
                    activeNetworkInfo.isAvailable
                }
            }
            return false
        }

        fun isWifiAvailable(context: Context): Boolean {
            if (context != null) {
                val mConnectivityManager = context
                        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                if (mWiFiNetworkInfo != null) {
                    return mWiFiNetworkInfo.isAvailable
                }
            }
            return false
        }

        fun getConnectName(context: Context): (String) {
            if (context != null) {
                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val connectionInfo = wifiManager.connectionInfo
                var name = connectionInfo.ssid.substring(1, connectionInfo.getSSID().length - 1)
                return name
            }
            return ""
        }

        fun getIP(context: Context): String? {
            try {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                            return inetAddress.getHostAddress().toString()
                        }
                    }
                }
            } catch (ex: SocketException) {
                ex.printStackTrace()
            }

            return null
        }

        fun getLocalIpAddress(context: Context): String? {
            val wifiManager = context.getSystemService(android.content.Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val ipAddress = wifiInfo.ipAddress
            try {
                return InetAddress.getByName(String.format("%d.%d.%d.%d",
                        ipAddress and 0xff, ipAddress shr 8 and 0xff,
                        ipAddress shr 16 and 0xff, ipAddress shr 24 and 0xff)).toString()
            } catch (e: UnknownHostException) {

                e.printStackTrace()
            }
            return null
        }
    }
}