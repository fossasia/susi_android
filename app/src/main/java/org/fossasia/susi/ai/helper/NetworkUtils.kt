package org.fossasia.susi.ai.helper

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by chiragw15 on 10/7/17.
 */
class NetworkUtils {
    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivityManager.activeNetworkInfo
        return info != null && info.isConnected
    }
}