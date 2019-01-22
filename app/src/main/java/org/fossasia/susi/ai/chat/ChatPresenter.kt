package org.fossasia.susi.ai.chat

import android.content.Context
import android.os.Handler
import org.fossasia.susi.ai.BuildConfig
import org.fossasia.susi.ai.MainApplication
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.contract.IChatPresenter
import org.fossasia.susi.ai.chat.contract.IChatView
import org.fossasia.susi.ai.data.ChatModel
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.contract.IChatModel
import org.fossasia.susi.ai.data.db.ChatArgs
import org.fossasia.susi.ai.data.db.DatabaseRepository
import org.fossasia.susi.ai.data.db.contract.IDatabaseRepository
import org.fossasia.susi.ai.data.model.TableItem
import org.fossasia.susi.ai.helper.LocationHelper
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.helper.DateTimeHelper
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.NetworkUtils
import org.fossasia.susi.ai.rest.clients.BaseUrl
import org.fossasia.susi.ai.rest.responses.others.LocationResponse
import org.fossasia.susi.ai.rest.responses.susi.MemoryResponse
import org.fossasia.susi.ai.rest.responses.susi.SusiResponse
import retrofit2.Response
import timber.log.Timber
import java.util.LinkedList
import java.util.Locale
import java.util.TimeZone
import java.util.Date
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.HashMap

/**
 * Presentation Layer for Chat View.
 *
 * The P in MVP
 * Created by chiragw15 on 9/7/17.
 */
class ChatPresenter(context: Context) : IChatPresenter, IChatModel.OnRetrievingMessagesFinishedListener,
        IChatModel.OnLocationFromIPReceivedListener, IChatModel.OnMessageFromSusiReceivedListener,
        IDatabaseRepository.OnDatabaseUpdateListener {

    private var chatView: IChatView? = null
    var chatModel: IChatModel = ChatModel()
    private var utilModel: UtilModel = UtilModel(context)
    private var databaseRepository: IDatabaseRepository = DatabaseRepository()
    private lateinit var locationHelper: LocationHelper
    private val nonDeliveredMessages = LinkedList<Pair<String?, Long>>()
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
    var id: Long = 0
    var identifier: String = ""
    var tableItem: TableItem? = null

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
        micCheck = PrefManager.getBoolean(R.string.setting_mic_key, true)
        chatView?.checkMicPref(PrefManager.getBoolean(R.string.setting_mic_key, true))
        chatView?.checkEnterKeyPref(PrefManager.getBoolean(R.string.settings_enterPreference_key, false))
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
        if (BuildConfig.FLAVOR.equals("fdroid"))
            return
        val view = chatView
        if (view != null) {
            if (view.checkPermission(utilModel.permissionsToGet()[2]) &&
                    view.checkPermission(utilModel.permissionsToGet()[1])) {
                if (utilModel.isArmDevice() && utilModel.checkMicInput()) {
                    utilModel.copyAssetstoSD()
                    chatView?.initHotword()
                    startHotwordDetection()
                } else {
                    utilModel.putBooleanPref(R.string.hotword_detection_key, false)
                    if (utilModel.getBooleanPref(R.string.notify_user_key, true)) {
                        chatView?.showToast(utilModel.getString(R.string.error_hotword))
                        utilModel.putBooleanPref(R.string.notify_user_key, false)
                    }
                }
            }
        }
    }

    override fun hotwordDetected() {
        chatView?.promptSpeechInput()
    }

    override fun startHotwordDetection() {
        if (!isDetectionOn && utilModel.getBooleanPref(R.string.hotword_detection_key, false)) {
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
            PrefManager.putBoolean(R.string.setting_mic_key, false)
        } else {
            micCheck = utilModel.checkMicInput()
            PrefManager.putBoolean(R.string.setting_mic_key, utilModel.checkMicInput())
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
        val memoryResponse = response?.body()
        if (response != null && response.isSuccessful && memoryResponse != null) {
            val allMessages = memoryResponse.cognitionsList
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
                        databaseRepository.updateDatabase(ChatArgs(
                                prevId = newMessageIndex,
                                isDate = true,
                                date = DateTimeHelper.getDate(queryDate),
                                timeStamp = DateTimeHelper.getTime(queryDate)),
                                this)
                    } else {
                        val prevDate = DateTimeHelper.getDate(allMessages[i + 1].queryDate)

                        if (DateTimeHelper.getDate(queryDate) != prevDate) {
                            databaseRepository.updateDatabase(ChatArgs(
                                    prevId = newMessageIndex,
                                    isDate = true,
                                    date = DateTimeHelper.getDate(queryDate),
                                    timeStamp = DateTimeHelper.getTime(queryDate)),
                                    this)
                        }
                    }

                    c = newMessageIndex
                    databaseRepository.updateDatabase(ChatArgs(
                            prevId = newMessageIndex,
                            message = query,
                            date = DateTimeHelper.getDate(queryDate),
                            timeStamp = DateTimeHelper.getTime(queryDate),
                            mine = true,
                            isHavingLink = isHavingLink),
                            this)

                    if (allMessages[i].answers.isEmpty()) {
                        databaseRepository.updateDatabase(ChatArgs(
                                prevId = c,
                                message = utilModel.getString(R.string.error_internet_connectivity),
                                date = DateTimeHelper.date,
                                timeStamp = DateTimeHelper.currentTime,
                                actionType = Constant.ANSWER),
                                this)
                        continue
                    }

                    val actionSize = allMessages[i].answers[0].actions.size

                    for (j in 0 until actionSize) {
                        val psh = ParseSusiResponseHelper()
                        psh.parseSusiResponse(allMessages[i], j, utilModel.getString(R.string.error_occurred_try_again))
                        try {
                            databaseRepository.updateDatabase(ChatArgs(prevId = c,
                                    message = psh.answer,
                                    date = DateTimeHelper.getDate(answerDate),
                                    timeStamp = DateTimeHelper.getTime(answerDate),
                                    actionType = psh.actionType,
                                    mapData = psh.mapData,
                                    isHavingLink = psh.isHavingLink,
                                    datumList = psh.datumList,
                                    webSearch = psh.webSearch,
                                    tableItem = psh.tableData,
                                    identifier = psh.identifier,
                                    skillLocation = allMessages[i].answers[0].skills[0]),
                                    this)
                        } catch (e: Exception) {
                            Timber.e(e)
                            databaseRepository.updateDatabase(ChatArgs(
                                    prevId = c,
                                    message = utilModel.getString(R.string.error_internet_connectivity),
                                    date = DateTimeHelper.date,
                                    timeStamp = DateTimeHelper.currentTime,
                                    actionType = Constant.ANSWER
                            ), this)
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
        val locationResponse = response.body()
        if (response.isSuccessful && locationResponse != null) {
            try {
                val loc = locationResponse.loc
                val s = loc.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                latitude = s[0].toDouble()
                longitude = s[1].toDouble()
                source = Constant.IP
                countryCode = locationResponse.country
                val locale = Locale("", countryCode)
                countryName = locale.displayCountry
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    //Gets Location of user using gps and network
    override fun getLocationFromLocationService() {
        locationHelper = LocationHelper(MainApplication.instance.applicationContext)
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
            databaseRepository.updateDatabase(ChatArgs(prevId = newMessageIndex,
                    isDate = true,
                    date = DateTimeHelper.date,
                    timeStamp = DateTimeHelper.currentTime),
                    this)
        } else {
            val s = databaseRepository.getAMessage(newMessageIndex - 1)?.date
            if (DateTimeHelper.date != s) {
                databaseRepository.updateDatabase(ChatArgs(
                        prevId = newMessageIndex,
                        isDate = true,
                        date = DateTimeHelper.date,
                        timeStamp = DateTimeHelper.currentTime),
                        this)
            }
        }
        nonDeliveredMessages.add(Pair(query, newMessageIndex))
        databaseRepository.updateDatabase(ChatArgs(
                prevId = newMessageIndex,
                message = actual.trim(),
                date = DateTimeHelper.date,
                timeStamp = DateTimeHelper.currentTime,
                mine = true,
                isHavingLink = isHavingLink
        ), this)
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
                val data: MutableMap<String, String?> = HashMap()
                data["timezoneOffset"] = timezoneOffset.toString()
                data["longitude"] = longitude.toString()
                data["latitude"] = latitude.toString()
                data["geosource"] = source
                data["language"] = language
                data["country_code"] = countryCode.toString()
                data["country_name"] = countryName.toString()
                data["device_type"] = deviceType
                data["q"] = query
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
            databaseRepository.updateDatabase(ChatArgs(
                    prevId = id,
                    message = utilModel.getString(R.string.error_internet_connectivity),
                    date = DateTimeHelper.date,
                    timeStamp = DateTimeHelper.currentTime,
                    actionType = Constant.ANSWER
            ),
                    this)
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
        val susiResponse = response?.body()
        if (response != null && response.isSuccessful && susiResponse != null) {
            if (susiResponse.answers.isEmpty()) {
                databaseRepository.updateDatabase(ChatArgs(
                        prevId = id,
                        message = utilModel.getString(R.string.error_internet_connectivity),
                        date = DateTimeHelper.date,
                        timeStamp = DateTimeHelper.currentTime,
                        actionType = Constant.ANSWER
                ),
                        this)
                return
            }

            val actionSize = susiResponse.answers[0].actions.size
            val date = susiResponse.answerDate

            for (i in 0 until actionSize) {
                val delay = susiResponse.answers[0].actions[i].delay
                val handler = Handler()
                handler.postDelayed({
                    val psh = ParseSusiResponseHelper()
                    psh.parseSusiResponse(susiResponse, i, utilModel.getString(R.string.error_occurred_try_again))

                    var setMessage = psh.answer
                    if (psh.actionType == Constant.TABLE) {
                        tableItem = psh.tableData
                    } else if (psh.actionType == Constant.VIDEOPLAY || psh.actionType == Constant.AUDIOPLAY) {
                        // Play youtube video
                        identifier = psh.identifier
                        chatView?.playVideo(identifier)
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
                        databaseRepository.updateDatabase(ChatArgs(
                                prevId = id,
                                message = setMessage,
                                date = DateTimeHelper.getDate(date),
                                timeStamp = DateTimeHelper.getTime(date),
                                actionType = psh.actionType,
                                mapData = psh.mapData,
                                isHavingLink = psh.isHavingLink,
                                datumList = psh.datumList,
                                webSearch = psh.webSearch,
                                tableItem = tableItem,
                                identifier = identifier,
                                skillLocation = susiResponse.answers[0].skills[0]
                        ), this)
                    } catch (e: Exception) {
                        Timber.e(e)
                        databaseRepository.updateDatabase(ChatArgs(
                                prevId = id,
                                message = utilModel.getString(R.string.error_internet_connectivity),
                                date = DateTimeHelper.date,
                                timeStamp = DateTimeHelper.currentTime,
                                actionType = Constant.ANSWER
                        ), this)
                    }
                }, delay)
            }
            chatView?.hideWaitingDots()
        } else {
            if (!NetworkUtils.isNetworkConnected()) {
                nonDeliveredMessages.addFirst(Pair(query, id))
                chatView?.displaySnackbar(utilModel.getString(R.string.no_internet_connection))
            } else {
                databaseRepository.updateDatabase(ChatArgs(
                        prevId = id,
                        message = utilModel.getString(R.string.error_internet_connectivity),
                        date = DateTimeHelper.date,
                        timeStamp = DateTimeHelper.currentTime,
                        actionType = Constant.ANSWER
                ), this)
            }
            chatView?.hideWaitingDots()
        }
        computeOtherMessage()
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
            PrefManager.putBoolean(R.string.setting_mic_key, utilModel.checkMicInput())
        }
    }

    override fun onDetach() {
        locationHelper.removeListener()
        databaseRepository.closeDatabase()
        chatView = null
    }
}