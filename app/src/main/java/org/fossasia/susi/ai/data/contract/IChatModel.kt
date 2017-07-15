package org.fossasia.susi.ai.data.contract

import org.fossasia.susi.ai.rest.responses.others.LocationResponse
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

    fun retrieveOldMessages(listener: OnRetrievingMessagesFinishedListener)
    fun getLocationFromIP(listener: OnLocationFromIPReceivedListener)
}