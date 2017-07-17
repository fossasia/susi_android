package org.fossasia.susi.ai.chat

import android.os.Handler

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
import org.fossasia.susi.ai.helper.*
import org.fossasia.susi.ai.rest.clients.BaseUrl
import org.fossasia.susi.ai.rest.responses.others.LocationResponse
import org.fossasia.susi.ai.rest.responses.susi.MemoryResponse
import org.fossasia.susi.ai.rest.responses.susi.SusiResponse

import retrofit2.Response

import java.util.*

/**
 * Presentation Layer for Chat View.
 *
 * The P in MVP
 * Created by chiragw15 on 9/7/17.
 */
class ChatPresenter(chatActivity: ChatActivity): IChatPresenter, IChatModel.OnRetrievingMessagesFinishedListener,
        IChatModel.OnLocationFromIPReceivedListener, IChatModel.OnMessageFromSusiReceivedListener,
        IDatabaseRepository.onDatabaseUpdateListener{

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
    var atHome = true
    var backPressedOnce = false

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

        chatView?.setupAdapter(databaseRepository.getAllMessages())

        getPermissions()

    }

    override fun checkPreferences() {
        micCheck = utilModel.getBooleanPref(Constant.MIC_INPUT, true)
        chatView?.checkMicPref(utilModel.getBooleanPref(Constant.MIC_INPUT, true))
        chatView?.checkEnterKeyPref(utilModel.getBooleanPref(Constant.ENTER_SEND, false))
    }

    override fun onMenuCreated() {
        chatView?.enableLoginInMenu(utilModel.getAnonymity())
    }

    override fun micCheck(): Boolean {
        return micCheck
    }

    override fun check(boolean: Boolean) {
        check = boolean
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

    override fun disableMicInput(boolean: Boolean) {
        if(boolean) {
            micCheck = false
            PrefManager.putBoolean(Constant.MIC_INPUT, false)
        } else {
            micCheck = utilModel.checkMicInput()
            PrefManager.putBoolean(Constant.MIC_INPUT, utilModel.checkMicInput())
            chatView?.checkMicPref(micCheck)
        }
    }

    //Retrieves old Messages
    override fun retrieveOldMessages(firstRun: Boolean) {
        if(firstRun and NetworkUtils.isNetworkConnected()) {
            chatView?.showRetrieveOldMessageProgress()
            val thread = object : Thread() {
                override fun run() {
                    chatModel.retrieveOldMessages(this@ChatPresenter)
                }
            }
            thread.start()
        }
    }

    override fun onRetrieveSuccess(response: Response<MemoryResponse>?) {
        if (response != null && response.isSuccessful && response.body() != null) {
            val allMessages = response.body().cognitionsList
            if (allMessages.isEmpty()) {
                chatView?.showToast("No messages found")
            } else {
                var c: Long
                for (i in allMessages.size - 1 downTo 0) {
                    val query = allMessages[i].query
                    val queryDate = allMessages[i].queryDate
                    val answerDate = allMessages[i].answerDate

                    val urlList = ParseSusiResponseHelper.extractUrls(query)
                    val isHavingLink = !urlList.isEmpty()

                    newMessageIndex = PrefManager.getLong(Constant.MESSAGE_COUNT, 0)

                    if (newMessageIndex == 0L) {
                        databaseRepository.updateDatabase(newMessageIndex, "", true, DateTimeHelper.getDate(queryDate),
                                DateTimeHelper.getTime(queryDate), false, "", null, false, null, "", 0, this)
                    } else {
                        val prevDate = DateTimeHelper.getDate(allMessages[i + 1].queryDate)

                        if (DateTimeHelper.getDate(queryDate) != prevDate) {
                            databaseRepository.updateDatabase(newMessageIndex, "", true, DateTimeHelper.getDate(queryDate),
                                    DateTimeHelper.getTime(queryDate), false, "", null, false, null, "", 0, this)
                        }
                    }

                    c = newMessageIndex
                    databaseRepository.updateDatabase(newMessageIndex, query, false, DateTimeHelper.getDate(queryDate),
                            DateTimeHelper.getTime(queryDate), true, "", null, isHavingLink, null, "", 0, this)

                    val actionSize = allMessages[i].answers[0].actions.size

                    for (j in 0..actionSize - 1) {
                        val psh = ParseSusiResponseHelper()
                        psh.parseSusiResponse(allMessages[i], j, utilModel.getString(R.string.error_occurred_try_again))
                        databaseRepository.updateDatabase(c, psh.answer, false, DateTimeHelper.getDate(answerDate),
                                DateTimeHelper.getTime(answerDate), false, psh.actionType, psh.mapData, psh.isHavingLink,
                                psh.datumList, psh.webSearch, psh.count, this)
                    }
                }
            }
        }
        chatView?.hideRetrieveOldMessageProgress()
    }

    override fun updateMessageCount() {
        newMessageIndex++
        PrefManager.putLong(Constant.MESSAGE_COUNT, newMessageIndex)
    }

    override fun onRetrieveFailure() {
        chatView?.hideRetrieveOldMessageProgress()
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
        val urlList = ParseSusiResponseHelper.extractUrls(query)
        val isHavingLink = !urlList.isEmpty()

        newMessageIndex = PrefManager.getLong(Constant.MESSAGE_COUNT, 0)

        if (newMessageIndex == 0L) {
            databaseRepository.updateDatabase(newMessageIndex, "", true, DateTimeHelper.date,
                    DateTimeHelper.currentTime, false, "", null, false, null, "", 0, this)
        } else {
            val s = databaseRepository.getAMessage(newMessageIndex-1).date
            if (DateTimeHelper.date != s) {
                databaseRepository.updateDatabase(newMessageIndex, "", true, DateTimeHelper.date,
                        DateTimeHelper.currentTime, false, "", null, false, null, "", 0, this)
            }
        }
        nonDeliveredMessages.add(Pair(query, newMessageIndex))
        databaseRepository.updateDatabase(newMessageIndex, actual, false, DateTimeHelper.date,
                DateTimeHelper.currentTime, true, "", null, isHavingLink, null, "", 0, this)
        getLocationFromLocationService()
        computeThread().start()
    }

    override fun startComputingThread() {
        computeThread().start()
    }

    private inner class computeThread : Thread() {
        override fun run() {
            computeOtherMessage()
        }
    }

    @Synchronized
    fun computeOtherMessage() {
        if (!nonDeliveredMessages.isEmpty()) {
            if (NetworkUtils.isNetworkConnected()) {
                chatView?.showWaitingDots()
                val tz = TimeZone.getDefault()
                val now = Date()
                val timezoneOffset = -1 * (tz.getOffset(now.time) / 60000)
                val query = nonDeliveredMessages.first.first

                chatModel.getSusiMessage(timezoneOffset, longitude, latitude, source, Locale.getDefault().language, query, this)

            } else run {
                chatView?.hideWaitingDots()
                chatView?.displaySnackbar(utilModel.getString(R.string.no_internet_connection))
            }
        }
    }

    override fun onSusiMessageReceivedFailure(t: Throwable) {
        chatView?.hideWaitingDots()
        val id = nonDeliveredMessages.first.second
        val query = nonDeliveredMessages.first.first
        nonDeliveredMessages.pop()

        if (!NetworkUtils.isNetworkConnected()) {
            nonDeliveredMessages.addFirst(Pair(query, id))
            chatView?.displaySnackbar(utilModel.getString(R.string.no_internet_connection))
        } else {
            databaseRepository.updateDatabase(id, utilModel.getString(R.string.error_internet_connectivity),
                    false, DateTimeHelper.date, DateTimeHelper.currentTime, false, Constant.ANSWER,
                    null, false, null, "", -1, this)
        }
        BaseUrl.updateBaseUrl(t)
        computeOtherMessage()
    }

    override fun onSusiMessageReceivedSuccess(response: Response<SusiResponse>?) {
        val id = nonDeliveredMessages.first.second
        val query = nonDeliveredMessages.first.first
        nonDeliveredMessages.pop()

        if (response != null && response.isSuccessful && response.body() != null) {
            val susiResponse = response.body()

            val actionSize = response.body().answers[0].actions.size
            val date = response.body().answerDate

            for (i in 0..actionSize - 1) {
                val delay = response.body().answers[0].actions[i].delay
                val actionNo = i
                val handler = Handler()
                handler.postDelayed({
                    val psh = ParseSusiResponseHelper()
                    psh.parseSusiResponse(susiResponse, actionNo, utilModel.getString(R.string.error_occurred_try_again))
                    val setMessage = psh.answer
                    if (psh.actionType == Constant.ANSWER && (PrefManager.checkSpeechOutputPref() && check || PrefManager.checkSpeechAlwaysPref())){
                        var speechReply = setMessage
                        if (psh.isHavingLink) {
                            speechReply = setMessage.substring(0, setMessage.indexOf("http"))
                        }
                        chatView?.voiceReply(speechReply)
                    }
                    databaseRepository.updateDatabase(id, setMessage, false, DateTimeHelper.getDate(date),
                            DateTimeHelper.getTime(date), false, psh.actionType, psh.mapData, psh.isHavingLink,
                            psh.datumList, psh.webSearch, psh.count, this)
                }, delay)
            }
            chatView?.hideWaitingDots()
        } else {
            if (!NetworkUtils.isNetworkConnected()) {
                nonDeliveredMessages.addFirst(Pair(query, id))
                chatView?.displaySnackbar(utilModel.getString(R.string.no_internet_connection))
            } else {
                databaseRepository.updateDatabase(id, utilModel.getString(R.string.error_internet_connectivity),
                        false, DateTimeHelper.date, DateTimeHelper.currentTime, false,
                        Constant.ANSWER, null, false, null, "", -1, this)
            }
            chatView?.hideWaitingDots()
        }
        if (!NetworkUtils.isNetworkConnected())
            computeOtherMessage()
    }

    override fun onDatabaseUpdateSuccess() {
        chatView?.databaseUpdated()
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

    override fun exitChatActivity() {
        if (atHome) {
            if (backPressedOnce) {
                chatView?.finishActivity()
                return
            }
            backPressedOnce = true
            chatView?.showToast(utilModel.getString(R.string.exit))
            Handler().postDelayed({ backPressedOnce = false }, 2000)
        } else if (!atHome) {
            atHome = true
        }
    }

    override fun onDetach() {
        locationHelper.removeListener()
        databaseRepository.closeDatabase()
        chatView = null
    }
}