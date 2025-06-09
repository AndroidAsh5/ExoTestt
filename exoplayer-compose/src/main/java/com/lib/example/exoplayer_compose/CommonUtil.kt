package com.lib.example.exoplayer_compose

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object CommonUtil {
    fun isConnectedToWifi(connectivityManager: ConnectivityManager): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }
}
