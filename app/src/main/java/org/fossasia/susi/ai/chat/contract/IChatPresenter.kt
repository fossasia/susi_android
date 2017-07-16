package org.fossasia.susi.ai.chat.contract

/**
 *
 * Created by chiragw15 on 9/7/17.
 */
interface IChatPresenter {

    //At the start of Activity
    fun onAttach(chatView: IChatView)
    fun retrieveOldMessages(firstRun: Boolean)
    fun getUndeliveredMessages()
    fun setUp()
    fun onMenuCreated()

    //Getting user location
    fun getLocationFromIP()
    fun getLocationFromLocationService()

    //Interaction with susi
    fun sendMessage(query: String, actual: String)

    //Setting background wallpaper
    fun setUpBackground()
    fun cropPicture(encodedImage: String)
    fun openSelectBackgroundDialog(which: Int)

    //Hotword Detection
    fun initiateHotwordDetection()
    fun stopHotwordDetection()
    fun startHotwordDetection()
    fun hotwordDetected()

    //STT and TTS
    fun startSpeechInput()

    //Search for messages
    fun startSearch()
    fun stopSearch()
    fun onSearchQuerySearched(query: String)
    fun searchUP()
    fun searchDown()

    //Login and Logout
    fun logout()
    fun login()
}