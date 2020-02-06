package org.fossasia.susi.ai.chat

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import android.os.Handler
import android.speech.tts.TextToSpeech
import java.util.Date
import java.util.LinkedList
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.HashMap
import org.fossasia.susi.ai.BuildConfig
import org.fossasia.susi.ai.MainApplication
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.ChatActivity.Companion.ALARM
import org.fossasia.susi.ai.chat.contract.IChatPresenter
import org.fossasia.susi.ai.chat.contract.IChatView
import org.fossasia.susi.ai.data.ChatModel
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.contract.IChatModel
import org.fossasia.susi.ai.data.db.ChatArgs
import org.fossasia.susi.ai.data.db.DatabaseRepository
import org.fossasia.susi.ai.data.db.contract.IDatabaseRepository
import org.fossasia.susi.ai.data.model.TableItem
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.DateTimeHelper
import org.fossasia.susi.ai.helper.LocationHelper
import org.fossasia.susi.ai.helper.NetworkUtils
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.rest.clients.BaseUrl
import org.fossasia.susi.ai.rest.responses.others.LocationResponse
import org.fossasia.susi.ai.rest.responses.susi.MemoryResponse
import org.fossasia.susi.ai.rest.responses.susi.SusiResponse
import retrofit2.Response
import timber.log.Timber

/**
 * Presentation Layer for Chat View.
 *
 * The P in MVP
 * Created by chiragw15 on 9/7/17.
 */
class ChatPresenter(context: Context, private val chatView: IChatView?) :
        IChatPresenter, IChatModel.OnRetrievingMessagesFinishedListener,
        IChatModel.OnLocationFromIPReceivedListener, IChatModel.OnMessageFromSusiReceivedListener,
        IDatabaseRepository.OnDatabaseUpdateListener {

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
    private val youtubeVid: IYoutubeVid = YoutubeVid(context)
    private var textToSpeech: TextToSpeech? = null
    private val context: Context = context
    private lateinit var handler: Handler

    @Volatile
    var queueExecuting = AtomicBoolean(false)

    override fun setUp() {

        // find total number of messages and find new message index
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

    // initiates hotword detection
    override fun initiateHotwordDetection() {
        if (BuildConfig.FLAVOR == "fdroid")
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

    // Retrieves old Messages
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
                var chat: Long
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

                    chat = newMessageIndex
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
                                prevId = chat,
                                message = utilModel.getString(R.string.error_internet_connectivity),
                                date = DateTimeHelper.date,
                                timeStamp = DateTimeHelper.currentTime,
                                actionType = Constant.ANSWER),
                                this)
                        continue
                    }

                    val actionSize = allMessages[i].answers[0].actions.size

                    for (j in 0 until actionSize) {
                        val parseSusiHelper = ParseSusiResponseHelper()
                        parseSusiHelper.parseSusiResponse(allMessages[i], j, utilModel.getString(R.string.error_occurred_try_again))
                        try {
                            databaseRepository.updateDatabase(ChatArgs(prevId = chat,
                                    message = parseSusiHelper.answer,
                                    date = DateTimeHelper.getDate(answerDate),
                                    timeStamp = DateTimeHelper.getTime(answerDate),
                                    actionType = parseSusiHelper.actionType,
                                    mapData = parseSusiHelper.mapData,
                                    isHavingLink = parseSusiHelper.isHavingLink,
                                    datumList = parseSusiHelper.datumList,
                                    webSearch = parseSusiHelper.webSearch,
                                    tableItem = parseSusiHelper.tableData,
                                    identifier = parseSusiHelper.identifier,
                                    skillLocation = allMessages[i].answers[0].skills[0]),
                                    this)
                        } catch (e: Exception) {
                            Timber.e("Error occured while updating the database - " + e)
                            databaseRepository.updateDatabase(ChatArgs(
                                    prevId = chat,
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

    // Gets Location of user using his IP Address
    override fun getLocationFromIP() {
        chatModel.getLocationFromIP(this)
    }

    override fun onLocationSuccess(response: Response<LocationResponse>) {
        val locationResponse = response.body()
        if (response.isSuccessful && locationResponse != null) {
            try {
                val location = locationResponse.loc
                val split = location.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                latitude = split[0].toDouble()
                longitude = split[1].toDouble()
                source = Constant.IP
                countryCode = locationResponse.country
                val locale = Locale("", countryCode)
                countryName = locale.displayCountry
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    // Gets Location of user using gps and network
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

    // get undelivered messages from database
    override fun getUndeliveredMessages() {
        nonDeliveredMessages.clear()

        val nonDelivered = databaseRepository.getUndeliveredMessages()

        nonDelivered.mapTo(nonDeliveredMessages) { Pair(it.content, it.id) }
    }

    // sends message to susi
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
            val date = databaseRepository.getAMessage(newMessageIndex - 1)?.date
            if (DateTimeHelper.date != date) {
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
                val timeZone = TimeZone.getDefault()
                val now = Date()
                val timeZoneOffset = -1 * (timeZone.getOffset(now.time) / 60000)
                val query = nonDeliveredMessages.first.first
                val language = if (PrefManager.getString(Constant.LANGUAGE, Constant.DEFAULT) == Constant.DEFAULT) Locale.getDefault().language else PrefManager.getString(Constant.LANGUAGE, Constant.DEFAULT)
                val data: MutableMap<String, String?> = HashMap()
                data["timezoneOffset"] = timeZoneOffset.toString()
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
            var planDelay = 0L

            for (i in 0 until actionSize) {

                if (susiResponse.answers[0].actions[i].plan_delay.toString().isNullOrEmpty()) {
                    planDelay = 0L
                } else {
                    planDelay = susiResponse.answers[0].actions[i].plan_delay
                }
                executeTask(planDelay, susiResponse, i, date)
                chatView?.hideWaitingDots()
            }
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

    override fun executeTask(planDelay: Long, susiResponse: SusiResponse, i: Int, date: String) {
        handler = Handler()
        try {
            handler.postDelayed({
                val parseSusiHelper = ParseSusiResponseHelper()
                parseSusiHelper.parseSusiResponse(susiResponse, i, utilModel.getString(R.string.error_occurred_try_again))
                var setMessage = parseSusiHelper.answer

                if (parseSusiHelper.actionType == Constant.TABLE) {
                    tableItem = parseSusiHelper.tableData
                } else if (parseSusiHelper.actionType == Constant.VIDEOPLAY || parseSusiHelper.actionType == Constant.AUDIOPLAY) {
                    // Play youtube video
                    identifier = parseSusiHelper.identifier
                    youtubeVid.playYoutubeVid(identifier)
                } else if (parseSusiHelper.actionType == Constant.ANSWER && (PrefManager.checkSpeechOutputPref() && check || PrefManager.checkSpeechAlwaysPref())) {
                    setMessage = parseSusiHelper.answer
                    try {
                        var speechReply = setMessage
                        Handler().post {
                            textToSpeech = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
                                if (status != TextToSpeech.ERROR) {
                                    val locale = textToSpeech?.language
                                    textToSpeech?.language = locale
                                    textToSpeech?.speak(speechReply, TextToSpeech.QUEUE_FLUSH, null)
                                    PrefManager.putBoolean(R.string.used_voice, true)
                                }
                            })
                        }
                    } catch (e: Exception) {
                        Timber.e("Error occured while trying to start text to speech engine - " + e)
                    }
                } else if (parseSusiHelper.actionType == Constant.STOP) {
                    setMessage = parseSusiHelper.stop
                    removeCallBacks()
                    chatView?.stopMic()
                }

                if (parseSusiHelper.answer == ALARM) {
                    playRingTone()
                }

                try {
                    databaseRepository.updateDatabase(ChatArgs(
                            prevId = id,
                            message = setMessage,
                            date = DateTimeHelper.getDate(date),
                            timeStamp = DateTimeHelper.getTime(date),
                            actionType = parseSusiHelper.actionType,
                            mapData = parseSusiHelper.mapData,
                            isHavingLink = parseSusiHelper.isHavingLink,
                            datumList = parseSusiHelper.datumList,
                            webSearch = parseSusiHelper.webSearch,
                            tableItem = tableItem,
                            identifier = identifier,
                            skillLocation = susiResponse.answers[0].skills[0]
                    ), this)
                } catch (e: Exception) {
                    Timber.e("Error occured while updating the database - " + e)
                    databaseRepository.updateDatabase(ChatArgs(
                            prevId = id,
                            message = utilModel.getString(R.string.error_internet_connectivity),
                            date = DateTimeHelper.date,
                            timeStamp = DateTimeHelper.currentTime,
                            actionType = Constant.ANSWER
                    ), this)
                }
            }, planDelay)
        } catch (e: java.lang.Exception) {
            Timber.e("Error while showing data - " + e)
        }
    }

    override fun onDatabaseUpdateSuccess() {
        chatView?.databaseUpdated()
    }

    // Asks for permissions from user
    private fun getPermissions() {
        val permissionsRequired = utilModel.permissionsToGet()

        val permissionsGranted = arrayOfNulls<String>(3)
        var counter = 0

        for (permission in permissionsRequired) {
            if (!(chatView?.checkPermission(permission) as Boolean)) {
                permissionsGranted[counter] = permission
                counter++
            }
        }

        if (counter > 0) {
            chatView?.askForPermission(permissionsGranted)
        }

        if (!(chatView?.checkPermission(permissionsRequired[1]) as Boolean)) {
            PrefManager.putBoolean(R.string.setting_mic_key, utilModel.checkMicInput())
        }
    }

    override fun playRingTone() {
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val r = RingtoneManager.getRingtone(context, notification)
            r.play()
        } catch (e: Exception) {
            Timber.e("Error playing alarm tone - " + e)
        }
    }

    override fun removeCallBacks() {
        handler.removeCallbacksAndMessages(null)
        try {
            val intent = Intent(context, ChatActivity::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 123, intent, 0)
            val alarmManager = context.getSystemService(ALARM_SERVICE) as? AlarmManager
            alarmManager?.cancel(pendingIntent)
        } catch (e: Exception) {
            Timber.e("Failed to stop alarm - " + e)
        }
    }

    override fun onDetach() {
        locationHelper.removeListener()
        databaseRepository.closeDatabase()
    }
}
