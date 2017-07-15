package org.fossasia.susi.ai.chat

/**
 * Created by chiragw15 on 9/7/17.
 */
interface IChatPresenter {

    fun onAttach(chatView: IChatView)
    fun retrieveOldMessages(firstRun: Boolean)
    fun getLocationFromIP()
    fun getUndeliveredMessages()
    fun sendMessage(query: String, actual: String)
    fun setUp()
    fun setUpBackground()
    fun initiateHotwordDetection()
    fun getLocationFromLocationService()
    fun cropPicture(encodedImage: String)
    fun openSelectBackgroundDialog(which: Int)
    fun stopHotwordDetection()
    fun startHotwordDetection()
    fun hotwordDetected()
    fun startSpeechInput()
    fun logout()
    fun login()
}