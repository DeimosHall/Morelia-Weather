package com.deimos.moreliaclima.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest

class MyNetwork(private val context: Context) {
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
    private val network = connectivityManager.activeNetwork
    private val capabilities = connectivityManager.getNetworkCapabilities(network)
    private val networkCallbacks = MyNetworkCallbacks(context)

    init {
        connectionListener()
    }

    fun isConnected(): Boolean {
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    fun getCapabilities(): NetworkCapabilities? {
        return capabilities
    }

    private fun connectionListener() {
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder().build(),
            networkCallbacks
        )
    }
}