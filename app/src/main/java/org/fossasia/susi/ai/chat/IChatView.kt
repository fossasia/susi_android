package org.fossasia.susi.ai.chat

import android.graphics.drawable.Drawable
import io.realm.RealmResults
import org.fossasia.susi.ai.data.model.ChatMessage

/**
 * Created by chiragw15 on 9/7/17.
 */
interface IChatView {

    fun setTheme(darkTheme: Boolean)
    fun checkPermission(permission: String): Boolean
    fun askForPermission(permissions: Array<String?>)
    fun checkMicPref(micCheck: Boolean)
    fun checkEnterKeyPref(isChecked: Boolean)
    fun setupAdapter(chatMessageDatabaseList: RealmResults<ChatMessage>)
    fun setChatBackground(bg: Drawable?)
    fun showToast(message: String)
    fun showVoiceDots()
    fun displayVoiceInput()
    fun hideVoiceInput()
    fun displayPartialSTT(text: String)
    fun startLoginActivity()
    fun openImagePickerActivity()
}