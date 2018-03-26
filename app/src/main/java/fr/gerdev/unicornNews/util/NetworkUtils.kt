package fr.gerdev.unicornNews.util

import android.content.Context
import android.net.ConnectivityManager

// ger 26/03/18
class NetworkUtils(private val context: Context) {
    fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}