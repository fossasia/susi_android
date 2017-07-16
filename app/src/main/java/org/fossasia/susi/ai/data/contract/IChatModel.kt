package org.fossasia.susi.ai.data.contract

import org.fossasia.susi.ai.rest.responses.others.LocationResponse
import org.fossasia.susi.ai.rest.responses.susi.SusiResponse
import org.fossasia.susi.ai.rest.services.SusiService
import retrofit2.Response

/**
 *
 * Created by chiragw15 on 9/7/17.
 */
interface IChatModel {

    interface OnRetrievingMessagesFinishedListener {
        fun onRetrieveSuccess(message: String)
    }

    interface OnLocationFromIPReceivedListener {
        fun onLocationSuccess(response: Response<LocationResponse>)
    }

    interface OnMessageFromSusiReceivedListener {
        fun onSusiMessageReceivedSuccess(response: Response<SusiResponse>?)
        fun onSusiMessageReceivedFailure(t: Throwable)
    }

    fun getSusiMessage(timezoneOffset: Int, longitude: Double, latitude: Double, source: String,
                       language: String, query: String, listener: OnMessageFromSusiReceivedListener)
    fun retrieveOldMessages(listener: OnRetrievingMessagesFinishedListener)
    fun getLocationFromIP(listener: OnLocationFromIPReceivedListener)
}