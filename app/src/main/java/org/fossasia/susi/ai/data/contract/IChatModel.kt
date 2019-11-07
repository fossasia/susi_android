package org.fossasia.susi.ai.data.contract

import org.fossasia.susi.ai.rest.responses.others.LocationResponse
import org.fossasia.susi.ai.rest.responses.susi.MemoryResponse
import org.fossasia.susi.ai.rest.responses.susi.SusiResponse
import retrofit2.Response

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

    fun getSusiMessage(map: Map<String, String?>, listener: OnMessageFromSusiReceivedListener)

    fun retrieveOldMessages(listener: OnRetrievingMessagesFinishedListener)
    fun getLocationFromIP(listener: OnLocationFromIPReceivedListener)
}
