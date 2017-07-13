package org.fossasia.susi.ai.chat

import android.content.Context

/**
 * Created by chiragw15 on 9/7/17.
 */
interface IChatPresenter {

    fun onAttach(chatView: IChatView, context: Context)
    fun retrieveOldMessages(firstRun: Boolean)
    fun getLocationFromIP()
    fun getUndeliveredMessages()
    fun sendMessage(query: String, actual: String)
    fun setUp()
    fun initiateHotwordDetection()
    fun getLocationFromLocationService(context: Context)
    fun compensateTTSDelay(context: Context)
}