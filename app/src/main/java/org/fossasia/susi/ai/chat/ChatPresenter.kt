package org.fossasia.susi.ai.chat

import io.realm.RealmResults
import org.fossasia.susi.ai.MainApplication
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.db.DatabaseRepository
import org.fossasia.susi.ai.data.db.contract.IDatabaseRepository
import org.fossasia.susi.ai.chat.contract.IChatPresenter
import org.fossasia.susi.ai.chat.contract.IChatView
import org.fossasia.susi.ai.data.ChatModel
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.contract.IChatModel
import org.fossasia.susi.ai.data.model.ChatMessage
import org.fossasia.susi.ai.helper.LocationHelper
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.NetworkUtils
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.rest.responses.others.LocationResponse

import retrofit2.Response

import java.util.*

/**
 *
 * Created by chiragw15 on 9/7/17.
 */
class ChatPresenter(chatActivity: ChatActivity): IChatPresenter, IChatModel.OnRetrievingMessagesFinishedListener,
        IChatModel.OnLocationFromIPReceivedListener {

    var chatView: IChatView?= null
    var chatModel: IChatModel = ChatModel()
    var utilModel: UtilModel = UtilModel(chatActivity)
    var databaseRepository: IDatabaseRepository = DatabaseRepository()
    lateinit var locationHelper: LocationHelper
    val nonDeliveredMessages = LinkedList<Pair<String, Long>>()
    lateinit var results: RealmResults<ChatMessage>
    var newMessageIndex: Long = 0
    var micCheck = false
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var source = Constant.IP
    var isDetectionOn = false
    var check = false
    var offset = 1
    var isEnabled = true

    override fun onAttach(chatView: IChatView) {
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

    }

    override fun onMenuCreated() {
        chatView?.enableLoginInMenu(utilModel.getAnonymity())
    }

    //Change Background Methods
    override fun setUpBackground() {
        val previouslyChatImage = PrefManager.getString(Constant.IMAGE_DATA, "")
        if (previouslyChatImage.equals(utilModel.getString(R.string.background_no_wall), ignoreCase = true)) {
            chatView?.setChatBackground(null)
        } else if (!previouslyChatImage.equals("", ignoreCase = true)) {
           chatView?.setChatBackground(utilModel.decodeImage(previouslyChatImage))
        } else {
            chatView?.setChatBackground(null)
        }
    }

    override fun openSelectBackgroundDialog(which: Int) {
        when (which) {
            0 -> {
                chatView?.openImagePickerActivity()
            }
            1 -> {
                PrefManager.putString(Constant.IMAGE_DATA, utilModel.getString(R.string.background_no_wall))
                setUpBackground()
            }
        }
    }

    override fun cropPicture(encodedImage: String) {
        PrefManager.putString(Constant.IMAGE_DATA, encodedImage)
        setUpBackground()
    }

    //initiates hotword detection
    override fun initiateHotwordDetection() {
        if (chatView!!.checkPermission(utilModel.permissionsToGet()[2]) &&
                chatView!!.checkPermission(utilModel.permissionsToGet()[1])) {
            if ( utilModel.isArmDevice() && utilModel.checkMicInput()) {
                utilModel.copyAssetstoSD()
                chatView?.initHotword()
                startHotwordDetection()
            }
            else {
                chatView?.showToast(utilModel.getString(R.string.error_hotword))
                utilModel.putBooleanPref(Constant.HOTWORD_DETECTION, false)
            }
        }
    }

    override fun hotwordDetected() {
        chatView?.displayVoiceInput()
        chatView?.promptSpeechInput()
    }

    override fun startHotwordDetection() {
        if (!isDetectionOn && utilModel.getBooleanPref(Constant.HOTWORD_DETECTION, false)) {
            chatView?.stopRecording()
            isDetectionOn = true
        }
    }

    override fun stopHotwordDetection() {
        if (isDetectionOn) {
            chatView?.stopRecording()
            isDetectionOn = false
        }
    }

    override fun startSpeechInput() {
        check = true
        chatView?.displayVoiceInput()
        chatView?.promptSpeechInput()
    }

    //Retrieves old Messages
    override fun retrieveOldMessages(firstRun: Boolean) {
        if(firstRun and NetworkUtils.isNetworkConnected()) {
            chatModel.retrieveOldMessages(this)
        }
    }

    override fun onRetrieveSuccess(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //Gets Location of user using his IP Address
    override fun getLocationFromIP() {
        chatModel.getLocationFromIP(this)
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

    //Gets Location of user using gps and network
    override fun getLocationFromLocationService() {
        locationHelper = LocationHelper(MainApplication.getInstance().applicationContext)
        getLocation()
    }

    fun getLocation() {
        locationHelper.getLocation()
        if (locationHelper.canGetLocation()) {
            latitude = locationHelper.latitude
            longitude = locationHelper.longitude
            source = locationHelper.source
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

    //Search methods. Used when search is enabled
    override fun startSearch() {
        chatView?.displaySearchElements(true)
        isEnabled = false
    }

    override fun stopSearch() {
        chatView?.modifyMenu(false)
        offset = 1
        chatView?.displaySearchElements(false)
    }

    override fun onSearchQuerySearched(query: String) {
        chatView?.displaySearchElements(true)
        results = databaseRepository.getSearchResults(query)
        offset = 1
        if (results.size > 0) {
            chatView?.modifyMenu(true)
            chatView?.searchMovement(results[results.size - offset].id.toInt())
        } else {
            chatView?.showToast(utilModel.getString(R.string.not_found))
        }
    }

    override fun searchUP() {
        offset++
        if (results.size - offset > -1) {
            chatView?.searchMovement(results[results.size - offset].id.toInt())
        } else {
            chatView?.showToast(utilModel.getString(R.string.nothing_up_matches_your_query))
            offset--
        }
    }

    override fun searchDown() {
        offset--
        if (results.size - offset < results.size) {
            chatView?.searchMovement(results[results.size - offset].id.toInt())
        } else {
            chatView?.showToast(utilModel.getString(R.string.nothing_down_matches_your_query))
            offset++
        }
    }

    //Asks for permissions from user
    fun getPermissions() {
        val permissionsRequired = utilModel.permissionsToGet()

        val permissionsGranted = arrayOfNulls<String>(3)
        var c = 0

        for(permission in permissionsRequired) {
            if(!(chatView?.checkPermission(permission) as Boolean)) {
                permissionsGranted[c] = permission
                c++
            }
        }

        if(c > 0) {
            chatView?.askForPermission(permissionsGranted)
        }

        if(!(chatView?.checkPermission(permissionsRequired[1]) as Boolean)) {
            PrefManager.putBoolean(Constant.MIC_INPUT, utilModel.checkMicInput())
        }
    }

    override fun logout() {
        utilModel.clearToken()
        databaseRepository.deleteAllMessages()
        chatView?.startLoginActivity()
    }

    override fun login() {
        utilModel.clearToken()
        utilModel.saveAnonymity(false)
        databaseRepository.deleteAllMessages()
        chatView?.startLoginActivity()
    }
}