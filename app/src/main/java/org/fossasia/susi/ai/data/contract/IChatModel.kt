package org.fossasia.susi.ai.data.contract

import org.fossasia.susi.ai.rest.responses.others.LocationResponse
import org.fossasia.susi.ai.rest.responses.susi.MemoryResponse
import org.fossasia.susi.ai.rest.responses.susi.SusiResponse
import retrofit2.Response

/**
 * The interface for Chat Model
 *
 * Created by chiragw15 on 9/7/17.
 */
interface IChatModel {

    interface OnRetrievingMessagesFinishedListener {
        fun onRetrieveSuccess(response: Response<MemoryResponse>?)
        fun onRetrieveFailure()
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