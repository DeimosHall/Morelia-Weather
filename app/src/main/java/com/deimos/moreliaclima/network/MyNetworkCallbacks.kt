package com.deimos.moreliaclima.network

import android.content.Context
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.widget.Toast

class MyNetworkCallbacks(private val context: Context): NetworkCallback() {
    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        //Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        //Toast.makeText(context, "Not connected", Toast.LENGTH_SHORT).show()
    }
}