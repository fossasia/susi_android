package org.fossasia.susi.ai.chat

import android.Manifest
import android.app.Application
import android.content.Context
import org.fossasia.susi.ai.activities.DatabaseRepository
import org.fossasia.susi.ai.activities.IDatabaseRepository
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.NetworkUtils
import org.fossasia.susi.ai.helper.PrefManager


/**
 * Created by chiragw15 on 9/7/17.
 */
class ChatPresenter: IChatPresenter, IChatInteractor.OnRetrievingMessagesFinishedListener,
        IChatInteractor.OnLocationFromIPReceivedListener {

    var chatView: IChatView?= null
    var chatInteractor: IChatInteractor = ChatInteractor()
    var databaseRepository: IDatabaseRepository = DatabaseRepository()
    var newMessageIndex: Long = 0
    var micCheck = false

    override fun onAttach(chatView: IChatView, context: Context) {
        this.chatView = chatView

        chatView.setTheme(PrefManager.getString(Constant.THEME, Constant.LIGHT) == Constant.DARK)

        newMessageIndex = databaseRepository.getMessageCount() + 1
        PrefManager.putLong(Constant.MESSAGE_COUNT, newMessageIndex)
        micCheck = PrefManager.checkMicInput(context)

        getPermissions(context)

    }

    //Retrieves old Messages
    override fun retrieveOldMessages(firstRun: Boolean) {
        if(firstRun and NetworkUtils.isNetworkConnected()) {
            chatInteractor.retrieveOldMessages(this)
        }
    }

    override fun onRetrieveError(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRetrieveSuccess(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showRetrieveProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideRetrieveProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //Gets Location of user using his IP Address
    override fun getLocationFromIP() {

    }

    override fun onLocationSuccess() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLocationError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //Asks for permissions from user
    fun getPermissions(context: Context) {
        val permissionsRequired = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                                          Manifest.permission.RECORD_AUDIO,
                                          Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val permissionsGranted = arrayOfNulls<String>(3)
        var c = 0

        for(permission in permissionsRequired) {
            if(!(chatView?.checkPermissions(permission) as Boolean)) {
                permissionsGranted[c] = permission
                c++;
            }
        }

        if(c > 0) {
            chatView?.askForPermission(permissionsGranted)
        }

        if(!(chatView?.checkPermissions(permissionsRequired[1]) as Boolean)) {
            PrefManager.putBoolean(Constant.MIC_INPUT, PrefManager.checkMicInput(context))
        }
    }

}