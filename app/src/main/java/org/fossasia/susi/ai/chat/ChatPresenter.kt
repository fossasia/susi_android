package org.fossasia.susi.ai.chat

import android.Manifest
import android.content.Context
import android.support.v4.util.Pair
import org.fossasia.susi.ai.activities.DatabaseRepository
import org.fossasia.susi.ai.activities.IDatabaseRepository
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.NetworkUtils
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.rest.responses.others.LocationResponse
import retrofit2.Response
import java.util.*

/**
 * Created by chiragw15 on 9/7/17.
 */
class ChatPresenter(chatActivity: ChatActivity): IChatPresenter, IChatInteractor.OnRetrievingMessagesFinishedListener,
        IChatInteractor.OnLocationFromIPReceivedListener {

    var chatView: IChatView?= null
    var chatInteractor: IChatInteractor = ChatInteractor()
    var utilModel: UtilModel = UtilModel(chatActivity)
    var databaseRepository: IDatabaseRepository = DatabaseRepository()
    var newMessageIndex: Long = 0
    var micCheck = false
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var source = Constant.IP
    private val nonDeliveredMessages = LinkedList<Pair<String, Long>>()

    override fun onAttach(chatView: IChatView, context: Context) {
        this.chatView = chatView
    }

    override fun setUp() {
        //set theme
        chatView?.setTheme(PrefManager.getString(Constant.THEME, Constant.LIGHT) == Constant.DARK)

        //find total number of messages and find new message index
        newMessageIndex = databaseRepository.getMessageCount() + 1
        PrefManager.putLong(Constant.MESSAGE_COUNT, newMessageIndex)
        micCheck = utilModel.checkMicInput()

        getPermissions()

        chatView?.checkMicPref(utilModel.getBooleanPref(Constant.MIC_INPUT, true))
        chatView?.checkEnterKeyPref(utilModel.getBooleanPref(Constant.ENTER_SEND, false))

        chatView?.setupAdapter(databaseRepository.getAllMessages())

        //set background
        chatView?.setChatBackground(PrefManager.getString(Constant.IMAGE_DATA, ""))
    }

    //Retrieves old Messages
    override fun retrieveOldMessages(firstRun: Boolean) {
        if(firstRun and NetworkUtils.isNetworkConnected()) {
            chatInteractor.retrieveOldMessages(this)
        }
    }

    override fun onRetrieveSuccess(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //Gets Location of user using his IP Address
    override fun getLocationFromIP() {
        chatInteractor.getLocationFromIP(this)
    }

    override fun onLocationSuccess(response: Response<LocationResponse>) {
        if (response.isSuccessful && response.body() != null) {
            try {
                val loc = response.body().loc
                val s = loc.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                latitude = s[0].toDouble()
                longitude = s[1].toDouble()
                source = Constant.IP
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //get undelivered messages from database
    override fun getUndeliveredMessages() {
        nonDeliveredMessages.clear()

        val nonDelivered = databaseRepository.getUndeliveredMessages()

        nonDelivered.mapTo(nonDeliveredMessages) { Pair(it.content, it.id) }
    }

    //sends message to susi
    override fun sendMessage(query: String, actual: String) {

    }

    //Asks for permissions from user
    fun getPermissions() {
        val permissionsRequired = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                                          Manifest.permission.RECORD_AUDIO,
                                          Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val permissionsGranted = arrayOfNulls<String>(3)
        var c = 0

        for(permission in permissionsRequired) {
            if(!(chatView?.checkPermissions(permission) as Boolean)) {
                permissionsGranted[c] = permission
                c++
            }
        }

        if(c > 0) {
            chatView?.askForPermission(permissionsGranted)
        }

        if(!(chatView?.checkPermissions(permissionsRequired[1]) as Boolean)) {
            PrefManager.putBoolean(Constant.MIC_INPUT, utilModel.checkMicInput())
        }
    }

}