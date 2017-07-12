package org.fossasia.susi.ai.helper

import android.content.Context
import android.net.ConnectivityManager
import org.fossasia.susi.ai.MainApplication

/**
 * Created by chiragw15 on 10/7/17.
 */
class NetworkUtils {
    companion object {
        fun isNetworkConnected(): Boolean {
            val connectivityManager = MainApplication.getInstance()
                    .applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = connectivityManager.activeNetworkInfo
            return info != null && info.isConnected
        }
    }
}