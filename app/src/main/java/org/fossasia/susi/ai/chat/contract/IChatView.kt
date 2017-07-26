package org.fossasia.susi.ai.chat.contract

import android.graphics.drawable.Drawable
import io.realm.RealmResults
import org.fossasia.susi.ai.data.model.ChatMessage

/**
 * The interface for ChatActivity
 *
 * Created by chiragw15 on 9/7/17.
 */
interface IChatView {

    fun setTheme(darkTheme: Boolean)
    fun checkPermission(permission: String): Boolean
    fun askForPermission(permissions: Array<String?>)
    fun checkMicPref(micCheck: Boolean)
    fun checkEnterKeyPref(isChecked: Boolean)
    fun setupAdapter(chatMessageDatabaseList: RealmResults<ChatMessage>)
    fun enableLoginInMenu(isVisible: Boolean)
    fun setChatBackground(bg: Drawable?)
    fun showToast(message: String)
    fun showVoiceDots()
    fun displayVoiceInput()
    fun hideVoiceInput()
    fun voiceReply(reply: String)
    fun showRetrieveOldMessageProgress()
    fun hideRetrieveOldMessageProgress()
    fun showWaitingDots()
    fun hideWaitingDots()
    fun databaseUpdated()
    fun displaySnackbar(message: String)
    fun displayPartialSTT(text: String)
    fun startLoginActivity()
    fun openImagePickerActivity()
    fun startRecording()
    fun stopRecording()
    fun initHotword()
    fun promptSpeechInput()
    fun displaySearchElements(isSearchEnabled: Boolean)
    fun modifyMenu(show: Boolean)
    fun searchMovement(position: Int)
    fun finishActivity()
}