package org.fossasia.susi.ai.chat

import android.os.Handler
import org.fossasia.susi.ai.MainApplication
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.contract.IChatPresenter
import org.fossasia.susi.ai.chat.contract.IChatView
import org.fossasia.susi.ai.data.ChatModel
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.contract.IChatModel
import org.fossasia.susi.ai.data.db.DatabaseRepository
import org.fossasia.susi.ai.data.db.contract.IDatabaseRepository
import org.fossasia.susi.ai.helper.*
import org.fossasia.susi.ai.rest.clients.BaseUrl
import org.fossasia.susi.ai.rest.responses.others.LocationResponse
import org.fossasia.susi.ai.rest.responses.susi.MemoryResponse
import org.fossasia.susi.ai.rest.responses.susi.SusiResponse
import org.fossasia.susi.ai.rest.responses.susi.TableSusiResponse
import retrofit2.Response
import timber.log.Timber
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.HashMap

/**
 * Presentation Layer for Chat View.
 *
 * The P in MVP
 * Created by chiragw15 on 9/7/17.
 */
class ChatPresenter(chatActivity: ChatActivity) : IChatPresenter, IChatModel.OnRetrievingMessagesFinishedListener,
        IChatModel.OnLocationFromIPReceivedListener, IChatModel.OnMessageFromSusiReceivedListener,
        IDatabaseRepository.OnDatabaseUpdateListener {

    private var chatView: IChatView? = null
    var chatModel: IChatModel = ChatModel()
    private var utilModel: UtilModel = UtilModel(chatActivity)
    private var databaseRepository: IDatabaseRepository = DatabaseRepository()
    private lateinit var locationHelper: LocationHelper
    private val nonDeliveredMessages = LinkedList<Pair<String, Long>>()
    private var newMessageIndex: Long = 0
    private var micCheck = false
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    private var countryName: String? = ""
    private var countryCode: String? = ""
    private val deviceType = "Android"
    private var source = Constant.IP
    private var isDetectionOn = false
    var check = false
    var atHome = true
    var backPressedOnce = false
    var id: Long = 0;
    @Volatile
    var queueExecuting = AtomicBoolean(false)

    override fun onAttach(chatView: IChatView) {
        this.chatView = chatView
    }

    override fun setUp() {

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

    override fun micCheck(): Boolean {
        return micCheck
    }

    override fun micCheck(boolean: Boolean) {
        micCheck = boolean
    }

    override fun check(boolean: Boolean) {
        check = boolean
    }

    //initiates hotword detection
    override fun initiateHotwordDetection() {
        if (chatView!!.checkPermission(utilModel.permissionsToGet()[2]) &&
                chatView!!.checkPermission(utilModel.permissionsToGet()[1])) {
            if (utilModel.isArmDevice() && utilModel.checkMicInput()) {
                utilModel.copyAssetstoSD()
                chatView?.initHotword()
                startHotwordDetection()
            } else {
                utilModel.putBooleanPref(Constant.HOTWORD_DETECTION, false)
                if (utilModel.getBooleanPref(Constant.NOTIFY_USER, true)) {
                    chatView?.showToast(utilModel.getString(R.string.error_hotword))
                    utilModel.putBooleanPref(Constant.NOTIFY_USER, false)
                }
            }
        }
    }

    override fun hotwordDetected() {
        chatView?.promptSpeechInput()
    }

    override fun startHotwordDetection() {
        if (!isDetectionOn && utilModel.getBooleanPref(Constant.HOTWORD_DETECTION, false)) {
            chatView?.startRecording()
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
        chatView?.promptSpeechInput()
    }

    override fun disableMicInput(boolean: Boolean) {
        if (boolean) {
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
        if (firstRun and NetworkUtils.isNetworkConnected()) {
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
                                DateTimeHelper.getTime(queryDate), false, "", null, false, null, "", null, "", this)
                    } else {
                        val prevDate = DateTimeHelper.getDate(allMessages[i + 1].queryDate)

                        if (DateTimeHelper.getDate(queryDate) != prevDate) {
                            databaseRepository.updateDatabase(newMessageIndex, "", true, DateTimeHelper.getDate(queryDate),
                                    DateTimeHelper.getTime(queryDate), false, "", null, false, null, "", null, "", this)
                        }
                    }

                    c = newMessageIndex
                    databaseRepository.updateDatabase(newMessageIndex, query, false, DateTimeHelper.getDate(queryDate),
                            DateTimeHelper.getTime(queryDate), true, "", null, isHavingLink, null, "", null, "", this)

                    if (allMessages[i].answers.isEmpty()) {
                        databaseRepository.updateDatabase(c, utilModel.getString(R.string.error_internet_connectivity),
                                false, DateTimeHelper.date, DateTimeHelper.currentTime, false,
                                Constant.ANSWER, null, false, null, "", null, "", this)
                        continue
                    }

                    val actionSize = allMessages[i].answers[0].actions.size

                    for (j in 0 until actionSize) {
                        val psh = ParseSusiResponseHelper()
                        psh.parseSusiResponse(allMessages[i], j, utilModel.getString(R.string.error_occurred_try_again))
                        try {
                            databaseRepository.updateDatabase(c, psh.answer, false, DateTimeHelper.getDate(answerDate),
                                    DateTimeHelper.getTime(answerDate), false, psh.actionType, psh.mapData, psh.isHavingLink,
                                    psh.datumList, psh.webSearch, null, allMessages[i].answers[0].skills[0], this)
                        } catch (e: Exception) {
                            Timber.e(e)
                            databaseRepository.updateDatabase(c, utilModel.getString(R.string.error_internet_connectivity),
                                    false, DateTimeHelper.date, DateTimeHelper.currentTime, false,
                                    Constant.ANSWER, null, false, null, "", null, "", this)
                        }
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
                countryCode = response.body().country
                val locale = Locale("", countryCode)
                countryName = locale.displayCountry
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    //Gets Location of user using gps and network
    override fun getLocationFromLocationService() {
        locationHelper = LocationHelper(MainApplication.getInstance().applicationContext)
        getLocation()
    }

    private fun getLocation() {
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
        addToNonDeliveredList(query, actual)
        ComputeThread().start()
    }

    override fun addToNonDeliveredList(query: String, actual: String) {
        val urlList = ParseSusiResponseHelper.extractUrls(query)
        val isHavingLink = !urlList.isEmpty()

        newMessageIndex = PrefManager.getLong(Constant.MESSAGE_COUNT, 0)

        if (newMessageIndex == 0L) {
            databaseRepository.updateDatabase(newMessageIndex, "", true, DateTimeHelper.date,
                    DateTimeHelper.currentTime, false, "", null, false, null, "", null, "", this)
        } else {
            val s = databaseRepository.getAMessage(newMessageIndex - 1)?.date
            if (DateTimeHelper.date != s) {
                databaseRepository.updateDatabase(newMessageIndex, "", true, DateTimeHelper.date,
                        DateTimeHelper.currentTime, false, "", null, false, null, "", null, "", this)
            }
        }
        nonDeliveredMessages.add(Pair(query, newMessageIndex))
        databaseRepository.updateDatabase(newMessageIndex, actual, false, DateTimeHelper.date,
                DateTimeHelper.currentTime, true, "", null, isHavingLink, null, "", null, "", this)
        getLocationFromLocationService()
    }

    override fun startComputingThread() {
        ComputeThread().start()
    }

    private inner class ComputeThread : Thread() {
        override fun run() {
            if (queueExecuting.compareAndSet(false, true)) {
                computeOtherMessage()
            }
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
                val language = if (PrefManager.getString(Constant.LANGUAGE, Constant.DEFAULT) == Constant.DEFAULT) Locale.getDefault().language else PrefManager.getString(Constant.LANGUAGE, Constant.DEFAULT)
                val data: MutableMap<String, String> = HashMap()
                data.put("timezoneOffset", timezoneOffset.toString())
                data.put("longitude", longitude.toString())
                data.put("latitude", latitude.toString())
                data.put("geosource", source)
                data.put("language", language)
                data.put("country_code", countryCode.toString())
                data.put("country_name", countryName.toString())
                data.put("device_type", deviceType)
                data.put("q", query)
                chatModel.getSusiMessage(data, this)

            } else run {
                queueExecuting.set(false)
                chatView?.hideWaitingDots()
                chatView?.displaySnackbar(utilModel.getString(R.string.no_internet_connection))
            }
        } else {
            queueExecuting.set(false)
        }
    }

    override fun onSusiMessageReceivedFailure(t: Throwable) {
        chatView?.hideWaitingDots()

        if (nonDeliveredMessages.isEmpty())
            return

        val id = nonDeliveredMessages.first.second
        val query = nonDeliveredMessages.first.first
        nonDeliveredMessages.pop()

        if (!NetworkUtils.isNetworkConnected()) {
            nonDeliveredMessages.addFirst(Pair(query, id))
            chatView?.displaySnackbar(utilModel.getString(R.string.no_internet_connection))
        } else {
            databaseRepository.updateDatabase(id, utilModel.getString(R.string.error_internet_connectivity),
                    false, DateTimeHelper.date, DateTimeHelper.currentTime, false, Constant.ANSWER,
                    null, false, null, "", null, "", this)
        }
        BaseUrl.updateBaseUrl(t)
        computeOtherMessage()
    }

    override fun onSusiMessageReceivedSuccess(response: Response<SusiResponse>?) {

        if (nonDeliveredMessages.isEmpty())
            return

        id = nonDeliveredMessages.first.second
        val query = nonDeliveredMessages.first.first
        nonDeliveredMessages.pop()

        if (response != null && response.isSuccessful && response.body() != null) {
            val susiResponse = response.body()

            if (response.body().answers.isEmpty()) {
                databaseRepository.updateDatabase(id, utilModel.getString(R.string.error_internet_connectivity),
                        false, DateTimeHelper.date, DateTimeHelper.currentTime, false,
                        Constant.ANSWER, null, false, null, "", null, "", this)
                return
            }

            val actionSize = response.body().answers[0].actions.size
            val date = response.body().answerDate

            for (i in 0 until actionSize) {
                if (response.body().answers[0].actions[i].type.equals("table")) {
                    tableResponse(query)
                    return
                }
                val delay = response.body().answers[0].actions[i].delay
                val handler = Handler()
                handler.postDelayed({
                    val psh = ParseSusiResponseHelper()
                    psh.parseSusiResponse(susiResponse, i, utilModel.getString(R.string.error_occurred_try_again))

                    var setMessage = psh.answer
                    if (psh.actionType == Constant.VIDEOPLAY || psh.actionType == Constant.AUDIOPLAY) {
                        // Play youtube video
                        Timber.d("Playing a youtube video")
                        return@postDelayed

                    } else if (psh.actionType == Constant.ANSWER && (PrefManager.checkSpeechOutputPref() && check || PrefManager.checkSpeechAlwaysPref())) {
                        setMessage = psh.answer

                        var speechReply = setMessage
                        if (psh.isHavingLink) {
                            speechReply = setMessage.substring(0, setMessage.indexOf("http"))
                        }
                        chatView?.voiceReply(speechReply, susiResponse.answers[0].actions[i].language)
                    } else if (psh.actionType == Constant.STOP) {
                        setMessage = psh.stop
                        chatView?.stopMic()
                    }
                    try {
                        databaseRepository.updateDatabase(id, setMessage, false, DateTimeHelper.getDate(date),
                                DateTimeHelper.getTime(date), false, psh.actionType, psh.mapData, psh.isHavingLink,
                                psh.datumList, psh.webSearch, null, susiResponse.answers[0].skills[0], this)
                    } catch (e: Exception) {
                        Timber.e(e)
                        databaseRepository.updateDatabase(id, utilModel.getString(R.string.error_internet_connectivity),
                                false, DateTimeHelper.date, DateTimeHelper.currentTime, false,
                                Constant.ANSWER, null, false, null, "", null, "", this)
                    }
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
                        Constant.ANSWER, null, false, null, "", null, "", this)
            }
            chatView?.hideWaitingDots()
        }
        computeOtherMessage()
    }

    // A callback to check when the Table Message was successfully received
    override fun onTableMessageReceivedSuccess(response: Response<TableSusiResponse>?) {
        if (response != null && response.isSuccessful && response.body() != null) {
            val tableresponse = response.body()
            val psh = ParseTableSusiResponseHelper()
            psh.parseSusiResponse(response)
            val date = response.body().answerDate
            databaseRepository.updateDatabase(id, " ", false, DateTimeHelper.getDate(date),
                    DateTimeHelper.getTime(date), false, Constant.TABLE, null, false,
                    null, " ", psh.tableData, tableresponse.answers[0].skills[0], this)
            chatView?.hideWaitingDots()
        }
    }

    // A function called when the actionType is the table response type
    fun tableResponse(query: String) {
        val tz = TimeZone.getDefault()
        val now = Date()
        val timezoneOffset = -1 * (tz.getOffset(now.time) / 60000)
        val language = if (PrefManager.getString(Constant.LANGUAGE, Constant.DEFAULT).equals(Constant.DEFAULT)) Locale.getDefault().language else PrefManager.getString(Constant.LANGUAGE, Constant.DEFAULT)
        val data: MutableMap<String, String> = HashMap()
        data.put("timezoneOffset", timezoneOffset.toString())
        data.put("longitude", longitude.toString())
        data.put("latitude", latitude.toString())
        data.put("geosource", source)
        data.put("language", language)
        data.put("country_code", countryCode.toString())
        data.put("country_name", countryName.toString())
        data.put("device_type", deviceType)
        data.put("q", query)
        chatModel.getTableSusiMessage(data, this)
    }

    override fun onDatabaseUpdateSuccess() {
        chatView?.databaseUpdated()
    }

    //Asks for permissions from user
    private fun getPermissions() {
        val permissionsRequired = utilModel.permissionsToGet()

        val permissionsGranted = arrayOfNulls<String>(3)
        var c = 0

        for (permission in permissionsRequired) {
            if (!(chatView?.checkPermission(permission) as Boolean)) {
                permissionsGranted[c] = permission
                c++
            }
        }

        if (c > 0) {
            chatView?.askForPermission(permissionsGranted)
        }

        if (!(chatView?.checkPermission(permissionsRequired[1]) as Boolean)) {
            PrefManager.putBoolean(Constant.MIC_INPUT, utilModel.checkMicInput())
        }
    }

    override fun onDetach() {
        locationHelper.removeListener()
        databaseRepository.closeDatabase()
        chatView = null
    }
}
