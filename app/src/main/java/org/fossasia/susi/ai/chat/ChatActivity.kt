package org.fossasia.susi.ai.chat

import ai.kitt.snowboy.MsgEnum
import ai.kitt.snowboy.audio.AudioDataSaver
import ai.kitt.snowboy.audio.RecordingThread
import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.AudioManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_chat.askSusiMessage
import kotlinx.android.synthetic.main.activity_chat.rv_chat_feed
import kotlinx.android.synthetic.main.activity_chat.btnSpeak
import kotlinx.android.synthetic.main.activity_chat.btnScrollToEnd
import kotlinx.android.synthetic.main.activity_chat.coordinator_layout
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.adapters.recycleradapters.ChatFeedRecyclerAdapter
import org.fossasia.susi.ai.chat.contract.IChatPresenter
import org.fossasia.susi.ai.chat.contract.IChatView
import org.fossasia.susi.ai.data.model.ChatMessage
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.helper.Utils.hideSoftKeyboard
import org.fossasia.susi.ai.skills.SkillsActivity
import timber.log.Timber
import java.util.Locale

/**
 * The Chat Activity. Does all the main processes including
 * getting location of user, searching, interacting with susi, Hotword Detection, STT and TTS
 *
 * The V in MVP
 * Created by chiragw15 on 9/7/17.
 */
class ChatActivity : AppCompatActivity(), IChatView {

    lateinit var chatPresenter: IChatPresenter
    lateinit var youtubeVid: IYoutubeVid
    private val PERM_REQ_CODE = 1
    private lateinit var recyclerAdapter: ChatFeedRecyclerAdapter
    private var textToSpeech: TextToSpeech? = null
    var recordingThread: RecordingThread? = null
    private lateinit var networkStateReceiver: BroadcastReceiver
    private lateinit var progressDialog: ProgressDialog
    private var example: String = ""
    private var isConfigurationChanged = false
    private val enterAsSend: Boolean by lazy {
        PrefManager.getBoolean(R.string.settings_enterPreference_key, false)
    }

    private val afChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            textToSpeech?.stop()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val firstRun = intent.getBooleanExtra(Constant.FIRST_TIME, false)

        chatPresenter = ChatPresenter(this)
        chatPresenter.onAttach(this)

        youtubeVid = YoutubeVid(this)
        setUpUI()
        initializationMethod(firstRun)

        example = if (intent.getStringExtra("example") != null) {
            intent.getStringExtra("example")
        } else {
            ""
        }

        networkStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                chatPresenter.startComputingThread()
            }
        }
    }

    // This method is used to set up the UI components
    // of Chat Activity like Adapter, Toolbar, Background, Theme, etc
    private fun setUpUI() {
        chatPresenter.setUp()
        chatPresenter.checkPreferences()
        setEditText()
    }

    // This method is used to call all other methods
    // which should run every time when the Chat Activity is started
    // like getting user location, initialization of TTS and hotword etc
    private fun initializationMethod(firstRun: Boolean) {
        chatPresenter.retrieveOldMessages(firstRun)
        chatPresenter.getLocationFromIP()
        chatPresenter.getLocationFromLocationService()
        chatPresenter.getUndeliveredMessages()
        chatPresenter.initiateHotwordDetection()
        compensateTTSDelay()
    }

    private fun setEditText() {
        val watch = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                //do whatever you want to do before text change in input edit text
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.toString().trim { it <= ' ' }.isNotEmpty() || !chatPresenter.micCheck()) {
                    btnSpeak.setImageResource(R.drawable.ic_send_fab)
                    btnSpeak.setOnClickListener({
                        chatPresenter.check(false)
                        val chatMessage = askSusiMessage.text.toString().trim({ it <= ' ' })
                        val splits = chatMessage.split("\n".toRegex()).dropLastWhile({ it.isEmpty() })
                        val message = splits.joinToString(" ")
                        if (!chatMessage.isEmpty()) {
                            chatPresenter.sendMessage(message, askSusiMessage.text.toString())
                            askSusiMessage.setText("")
                        }
                    })
                } else {
                    btnSpeak.setImageResource(R.drawable.ic_mic_24dp)
                    btnSpeak.setOnClickListener {
                        textToSpeech?.stop()
                        chatPresenter.startSpeechInput()
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {
                //do whatever you want to do after text change in input edit text
            }
        }

        askSusiMessage.setSingleLine(false)
        askSusiMessage.maxLines = 4
        askSusiMessage.isVerticalScrollBarEnabled = true
        askSusiMessage.setHorizontallyScrolling(false)

        askSusiMessage.addTextChangedListener(watch)

        askSusiMessage.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val message = askSusiMessage.text.toString().trim({ it <= ' ' })
                if (!message.isEmpty()) {
                    chatPresenter.sendMessage(message, askSusiMessage.text.toString())
                    askSusiMessage.setText("")
                }
                handled = true
            }
            handled
        }

        askSusiMessage.setOnKeyListener(View.OnKeyListener { view, i, keyEvent ->
            if (i == KeyEvent.KEYCODE_ENTER && enterAsSend &&
                    (keyEvent.action == KeyEvent.ACTION_UP || keyEvent.action == KeyEvent.ACTION_DOWN)) {
                val message = askSusiMessage.text.toString().trim({ it <= ' ' })
                if (!message.isEmpty()) {
                    chatPresenter.sendMessage(message, askSusiMessage.text.toString())
                    askSusiMessage.setText("")
                }
                return@OnKeyListener true
            }
            false
        })
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        isConfigurationChanged = true
    }

    override fun setupAdapter(chatMessageDatabaseList: RealmResults<ChatMessage>) {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true

        rv_chat_feed.layoutManager = linearLayoutManager
        rv_chat_feed.setHasFixedSize(false)
        recyclerAdapter = ChatFeedRecyclerAdapter(this, chatMessageDatabaseList, true)
        rv_chat_feed.adapter = recyclerAdapter

        rv_chat_feed.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (isConfigurationChanged) {
                isConfigurationChanged = false
            } else {
                if (bottom < oldBottom) {
                    rv_chat_feed.postDelayed({
                        val scroll = rv_chat_feed.adapter?.itemCount?.minus(1)
                        val scrollTo: Int
                        if (scroll != null) {
                            scrollTo = if (scroll >= 0) scroll else 0
                            rv_chat_feed.scrollToPosition(scrollTo)
                        }
                    }, 10)
                }
            }
        }

        rv_chat_feed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("RestrictedApi")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() < rv_chat_feed.adapter?.itemCount!!.minus(5)) {
                    btnScrollToEnd.isEnabled = true
                    btnScrollToEnd.visibility = View.VISIBLE
                } else {
                    btnScrollToEnd.isEnabled = false
                    btnScrollToEnd.visibility = View.GONE
                }
            }
        })
    }

    private fun compensateTTSDelay() {
        Handler().post {
            textToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
                if (status != TextToSpeech.ERROR) {
                    val locale = textToSpeech?.language
                    textToSpeech?.language = locale
                }
            })
        }
    }

    //Take user's speech as input and send the message
    override fun promptSpeechInput() {
        if (recordingThread != null) {
            chatPresenter.stopHotwordDetection()
        }
        hideSoftKeyboard(this, window.decorView)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.speechToTextFrame, STTfragment())
        ft.addToBackStack(null)
        ft.commit()
    }

    //Replies user with Speech
    override fun voiceReply(reply: String, language: String) {
        val audioFocus = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val handler = Handler()
        handler.post {
            val result = audioFocus.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(s: String) {
                        if (recordingThread != null)
                            chatPresenter.stopHotwordDetection()
                    }

                    override fun onDone(s: String) {
                        if (recordingThread != null)
                            chatPresenter.startHotwordDetection()
                    }

                    override fun onError(s: String) {
                        if (recordingThread != null)
                            chatPresenter.startHotwordDetection()
                    }
                })

                val ttsParams = HashMap<String, String>()
                ttsParams[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = this@ChatActivity.packageName
                textToSpeech?.language = Locale(language)
                textToSpeech?.speak(reply, TextToSpeech.QUEUE_FLUSH, ttsParams)
                audioFocus?.abandonAudioFocus(afChangeListener)
            }
        }
    }

    fun enableVoiceInput() {
        btnSpeak.setImageResource(R.drawable.ic_mic_24dp)
        btnSpeak.isEnabled = true
    }

    override fun showWaitingDots() {
        recyclerAdapter.showDots()
    }

    override fun hideWaitingDots() {
        recyclerAdapter.hideDots()
    }

    override fun databaseUpdated() {
        rv_chat_feed.smoothScrollToPosition(recyclerAdapter.itemCount)
        recyclerAdapter.notifyDataSetChanged()
    }

    override fun displaySnackbar(message: String) {
        val snackbar = Snackbar.make(coordinator_layout, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    //Initiates hotword detection
    override fun initHotword() {
        recordingThread = RecordingThread(object : Handler() {
            override fun handleMessage(msg: Message) {
                val message = MsgEnum.getMsgEnum(msg.what)
                when (message) {
                    MsgEnum.MSG_ACTIVE -> {
                        chatPresenter.hotwordDetected()
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
    }

    override fun startRecording() {
        recordingThread?.startRecording()
    }

    override fun stopRecording() {
        recordingThread?.stopRecording()
    }

    override fun checkMicPref(micCheck: Boolean) {
        if (micCheck) {
            chatPresenter.check(true)
            btnSpeak.setImageResource(R.drawable.ic_mic_24dp)
            btnSpeak.setOnClickListener({
                btnSpeak.isEnabled = false
                textToSpeech?.stop()
                chatPresenter.startSpeechInput()
            })
        } else {
            chatPresenter.check(false)
            btnSpeak.setImageResource(R.drawable.ic_send_fab)
            btnSpeak.setOnClickListener({
                val message = askSusiMessage.text.toString().trim({ it <= ' ' })
                if (!message.isEmpty()) {
                    chatPresenter.sendMessage(message, askSusiMessage.text.toString())
                    askSusiMessage.setText("")
                }
            })
        }
    }

    override fun checkEnterKeyPref(isChecked: Boolean) {
        if (isChecked) {
            askSusiMessage.imeOptions = EditorInfo.IME_ACTION_SEND
            askSusiMessage.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD //setting this programmatically works for all devices
        } else {
            askSusiMessage.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
        }
    }

    override fun checkPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun askForPermission(permissions: Array<String?>) {
        ActivityCompat.requestPermissions(this, permissions, PERM_REQ_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERM_REQ_CODE -> run {
                var audioPermissionGiven = false
                for (i in permissions.indices) {
                    when (permissions[i]) {
                        Manifest.permission.ACCESS_FINE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            chatPresenter.getLocationFromLocationService()
                        }

                        Manifest.permission.RECORD_AUDIO -> {
                            if (grantResults.isNotEmpty() && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                chatPresenter.disableMicInput(false)
                            } else {
                                chatPresenter.disableMicInput(true)
                            }
                            audioPermissionGiven = true
                        }

                        Manifest.permission.WRITE_EXTERNAL_STORAGE -> if (grantResults.size >= 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED && audioPermissionGiven) {
                            chatPresenter.initiateHotwordDetection()
                        }
                    }
                }
            }
        }
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun scrollToEnd(view: View) {
        rv_chat_feed.adapter?.itemCount?.minus(1)?.let { rv_chat_feed.smoothScrollToPosition(it) }
    }

    fun openSettings(view: View) { val i = Intent(this, SkillsActivity::class.java)
        startActivity(i)
        finish()
    }

    override fun showRetrieveOldMessageProgress() {
        progressDialog = ProgressDialog(this@ChatActivity)
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.dialog_retrieve_messages_title))
        progressDialog.show()
    }

    override fun hideRetrieveOldMessageProgress() {
        progressDialog.dismiss()
    }

    override fun finishActivity() {
        finish()
    }

    override fun onResume() {
        super.onResume()
        chatPresenter.getUndeliveredMessages()

        if (!example.isEmpty()) {
            chatPresenter.addToNonDeliveredList(example, example)
            example = ""
        }

        registerReceiver(networkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )

        if (recordingThread != null)
            chatPresenter.startHotwordDetection()

        if (askSusiMessage.text.toString().isNotEmpty()) {
            btnSpeak.setImageResource(R.drawable.ic_send_fab)
            askSusiMessage.setText("")
            chatPresenter.micCheck(false)
        }

        chatPresenter.checkPreferences()
    }

    fun setText(msg: String?) {
        chatPresenter.sendMessage(msg.toString(), msg.toString())
    }

    override fun onPause() {
        unregisterReceiver(networkStateReceiver)
        super.onPause()

        if (recordingThread != null)
            chatPresenter.stopHotwordDetection()

        textToSpeech?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        rv_chat_feed.clearOnScrollListeners()

        textToSpeech?.setOnUtteranceProgressListener(null)
        textToSpeech?.shutdown()
        chatPresenter.onDetach()
    }

    override fun stopMic() {
        onPause()
        registerReceiver(networkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )

        if (recordingThread != null)
            chatPresenter.startHotwordDetection()

        if (askSusiMessage.text.toString().isNotEmpty()) {
            btnSpeak.setImageResource(R.drawable.ic_send_fab)
            askSusiMessage.setText("")
            chatPresenter.micCheck(false)
        }

        chatPresenter.checkPreferences()
    }

    override fun playVideo(videoId: String) {
        Timber.d(videoId)
        youtubeVid.playYoutubeVid(videoId)
    }
}