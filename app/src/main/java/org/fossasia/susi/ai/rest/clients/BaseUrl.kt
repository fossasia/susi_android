package org.fossasia.susi.ai.rest.clients

import java.net.ConnectException
import java.net.SocketTimeoutException
import org.fossasia.susi.ai.helper.PrefManager
import timber.log.Timber

/**
 * <h1>Class to get Base URLs of susi/custom server.</h1>
 *
 *
 * Created by Rajan Maurya on 12/10/16.
 */
object BaseUrl {

    private const val PROTOCOL_HTTP = "https://"
    const val SUSI_DEFAULT_BASE_URL = "https://api.susi.ai"
    const val SUSI_SERVICES_URL = "https://config.asksusi.com"

    /**
     * This Method for updating the Susi Base url. first checking the index of current base url
     * from the list of baseUrls and if index != size() of list, it means that current base url
     * lies between the 0 to size() of base urls and just pick the next one base url and set in
     * SharedPreferences and if current base url is the last url in BaseUrl list then just pick
     * the first one base url and set in SharedPreferences.
     *
     * @param t the throwable
     */
    fun updateBaseUrl(t: Throwable) {
        val baseUrls = PrefManager.baseUrls
        if (baseUrls != null) {
            val indexOfUrl = baseUrls.susiServices.indexOf(PrefManager.susiRunningBaseUrl)
            if (indexOfUrl != baseUrls.susiServices.size) {
                PrefManager.susiRunningBaseUrl = BaseUrl.PROTOCOL_HTTP + baseUrls.susiServices[indexOfUrl + 1]
            } else {
                PrefManager.susiRunningBaseUrl = BaseUrl.PROTOCOL_HTTP + baseUrls.susiServices[0]
            }
            if (t is SocketTimeoutException || t is ConnectException) {
                Timber.e(t)
                // TODO: Fix this
                // ClientBuilder.createSusiService()
            }
        }
    }
}
