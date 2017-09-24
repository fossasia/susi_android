package org.fossasia.susi.ai.chat.contract

import io.realm.RealmResults
import org.fossasia.susi.ai.data.model.ChatMessage

/**
 * The interface for ChatActivity
 *
 * Created by chiragw15 on 9/7/17.
 */
interface IChatView {

    fun checkPermission(permission: String): Boolean
    fun askForPermission(permissions: Array<String?>)
    fun checkMicPref(micCheck: Boolean)
    fun checkEnterKeyPref(isChecked: Boolean)
    fun setupAdapter(chatMessageDatabaseList: RealmResults<ChatMessage>)
    fun showToast(message: String)
    fun voiceReply(reply: String, language: String)
    fun showRetrieveOldMessageProgress()
    fun hideRetrieveOldMessageProgress()
    fun showWaitingDots()
    fun hideWaitingDots()
    fun databaseUpdated()
    fun displaySnackbar(message: String)
    fun startRecording()
    fun stopRecording()
    fun initHotword()
    fun promptSpeechInput()
    fun finishActivity()
}