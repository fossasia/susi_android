package org.fossasia.susi.ai.chat

import ai.kitt.snowboy.MsgEnum
import ai.kitt.snowboy.audio.AudioDataSaver
import ai.kitt.snowboy.audio.RecordingThread

import android.content.Intent
import android.graphics.Bitmap
import android.os.*
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import org.fossasia.susi.ai.MainApplication

import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.activities.DatabaseRepository
import org.fossasia.susi.ai.activities.IDatabaseRepository
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.helper.*
import org.fossasia.susi.ai.rest.responses.others.LocationResponse

import retrofit2.Response

import java.util.*

/**
 *
 * Created by chiragw15 on 9/7/17.
 */
class ChatPresenter(chatActivity: ChatActivity): IChatPresenter, IChatInteractor.OnRetrievingMessagesFinishedListener,
        IChatInteractor.OnLocationFromIPReceivedListener {

    val TAG: String = ChatPresenter::class.java.name

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
    lateinit var locationHelper: LocationHelper
    lateinit var textToSpeech: TextToSpeech
    var recordingThread: RecordingThread? = null
    var isDetectionOn = false
    lateinit var recognizer: SpeechRecognizer

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
            if (Build.CPU_ABI.contains("arm") && !Build.FINGERPRINT.contains("generic") && utilModel.checkMicInput())
                initHotword()
            else {
                chatView?.showToast(utilModel.getString(R.string.error_hotword))
                utilModel.putBooleanPref(Constant.HOTWORD_DETECTION, false)
            }
        }
    }

    fun initHotword() {
        utilModel.copyAssetstoSD()

        recordingThread = RecordingThread(object : Handler() {
            override fun handleMessage(msg: Message) {
                val message = MsgEnum.getMsgEnum(msg.what)
                when (message) {
                    MsgEnum.MSG_ACTIVE -> {
                        chatView?.displayVoiceInput()
                        promptSpeechInput()
                    }
                    MsgEnum.MSG_INFO -> {
                    }
                    MsgEnum.MSG_VAD_SPEECH -> {
                    }
                    MsgEnum.MSG_VAD_NOSPEECH -> {
                    }
                    MsgEnum.MSG_ERROR -> {
                    }
                    else -> super.handleMessage(msg)
                }
            }
        }, AudioDataSaver())
        startHotwordDetection()
    }

    fun startHotwordDetection() {
        if (recordingThread != null && !isDetectionOn && utilModel.getBooleanPref(Constant.HOTWORD_DETECTION, false)) {
            recordingThread?.startRecording()
            isDetectionOn = true
        }
    }

    fun stopHotwordDetection() {
        if (recordingThread != null && isDetectionOn) {
            recordingThread?.stopRecording()
            isDetectionOn = false
        }
    }

    //Take user's speech as input and send the message
    private fun promptSpeechInput() {
        stopHotwordDetection()
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                "com.domain.app")
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

        recognizer = utilModel.createSpeechRecognizer()

        val listener = object : RecognitionListener {
            override fun onResults(results: Bundle) {
                val voiceResults = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (voiceResults == null) {
                    Log.e(TAG, "No voice results")
                } else {
                    Log.d(TAG, "Printing matches: ")
                    for (match in voiceResults) {
                        Log.d(TAG, match)
                    }
                }
                sendMessage(voiceResults[0], voiceResults[0])
                recognizer.destroy()
                chatView?.hideVoiceInput()
                startHotwordDetection()
            }

            override fun onReadyForSpeech(params: Bundle) {
                Log.d(TAG, "Ready for speech")
                chatView?.showVoiceDots()
            }

            override fun onError(error: Int) {
                Log.d(TAG, "Error listening for speech: " + error)
                chatView?.showToast("Could not recognize speech, try again.")
                recognizer.destroy()
                chatView?.hideVoiceInput()
                startHotwordDetection()
            }

            override fun onBeginningOfSpeech() {
                Log.d(TAG, "Speech starting")
            }

            override fun onBufferReceived(buffer: ByteArray) {
                // This method is intentionally empty
            }

            override fun onEndOfSpeech() {
                // This method is intentionally empty
            }

            override fun onEvent(eventType: Int, params: Bundle) {
                // This method is intentionally empty
            }

            override fun onPartialResults(partialResults: Bundle) {
                val partial = partialResults
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                chatView?.displayPartialSTT(partial[0])
            }

            override fun onRmsChanged(rmsdB: Float) {
                // This method is intentionally empty
            }
        }
        recognizer.setRecognitionListener(listener)
        recognizer.startListening(intent)
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

    override fun compensateTTSDelay() {
        Handler().post {
            textToSpeech = TextToSpeech(MainApplication.getInstance()
                    .applicationContext, TextToSpeech.OnInitListener { status ->
                if (status != TextToSpeech.ERROR) {
                    val locale = textToSpeech.getLanguage()
                    textToSpeech.language = locale
                }
            })
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