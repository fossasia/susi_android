package org.fossasia.susi.ai.chat

import ai.kitt.snowboy.MsgEnum
import ai.kitt.snowboy.audio.AudioDataSaver
import ai.kitt.snowboy.audio.RecordingThread

import android.Manifest
import android.app.ProgressDialog
import android.content.*
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.os.*
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
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

import io.realm.RealmResults

import kotlinx.android.synthetic.main.activity_chat.*

import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.adapters.recycleradapters.ChatFeedRecyclerAdapter
import org.fossasia.susi.ai.chat.contract.IChatPresenter
import org.fossasia.susi.ai.chat.contract.IChatView
import org.fossasia.susi.ai.data.model.ChatMessage
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.skills.SkillsActivity

import java.util.*

/**
 * The Chat Activity. Does all the main processes including
 * getting location of user, searching, interacting with susi, Hotword Detection, STT and TTS
 *
 * The V in MVP
 * Created by chiragw15 on 9/7/17.
 */
class ChatActivity: AppCompatActivity(), IChatView {

    val TAG: String = ChatActivity::class.java.name

    lateinit var chatPresenter: IChatPresenter
    val PERM_REQ_CODE = 1
    lateinit var recyclerAdapter: ChatFeedRecyclerAdapter
    var textToSpeech: TextToSpeech? = null
    var recordingThread: RecordingThread? = null
    lateinit var networkStateReceiver: BroadcastReceiver
    lateinit var progressDialog: ProgressDialog
    var example: String = ""

    val afChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
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

        setUpUI()
        initializationMethod(firstRun)

        if(intent.getStringExtra("example") != null) {
            example = intent.getStringExtra("example")
        } else {
            example = ""
        }

        networkStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                chatPresenter.startComputingThread()
            }
        }
    }

    // This method is used to set up the UI components
    // of Chat Activity like Adapter, Toolbar, Background, Theme, etc
    fun setUpUI() {
        chatPresenter.setUp()
        chatPresenter.checkPreferences()
        setEditText()
    }

    // This method is used to call all other methods
    // which should run every time when the Chat Activity is started
    // like getting user location, initialization of TTS and hotword etc
    fun initializationMethod(firstRun: Boolean) {
        chatPresenter.retrieveOldMessages(firstRun)
        chatPresenter.getLocationFromIP()
        chatPresenter.getLocationFromLocationService()
        chatPresenter.getUndeliveredMessages()
        chatPresenter.initiateHotwordDetection()
        compensateTTSDelay()
    }

    fun setEditText() {
        val watch = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                //do whatever you want to do before text change in input edit text
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.toString().trim { it <= ' ' }.isNotEmpty() || !chatPresenter.micCheck()) {
                    btnSpeak.setImageResource(R.drawable.ic_send_fab)
                    btnSpeak.setOnClickListener ({
                        chatPresenter.check(false)
                        val chat_message = et_message.text.toString().trim({ it <= ' ' })
                        val splits = chat_message.split("\n".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                        var message = ""
                        for (split in splits)
                            message = message + split + " "
                        if (!chat_message.isEmpty()) {
                            chatPresenter.sendMessage(message, et_message.text.toString())
                            et_message.setText("")
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

        et_message.setSingleLine(false)
        et_message.maxLines = 4
        et_message.isVerticalScrollBarEnabled = true
        et_message.setHorizontallyScrolling(false)

        et_message.addTextChangedListener(watch)

        et_message.setOnEditorActionListener({ _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val message = et_message.text.toString().trim({ it <= ' ' })
                if (!message.isEmpty()) {
                    chatPresenter.sendMessage(message, et_message.text.toString())
                    et_message.setText("")
                }
                handled = true
            }
            handled
        })
    }

    override fun setupAdapter(chatMessageDatabaseList: RealmResults<ChatMessage>) {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true

        rv_chat_feed.layoutManager = linearLayoutManager
        rv_chat_feed.setHasFixedSize(false)
        recyclerAdapter = ChatFeedRecyclerAdapter(this, chatMessageDatabaseList, true)
        rv_chat_feed.adapter = recyclerAdapter

        rv_chat_feed.addOnLayoutChangeListener({ _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                rv_chat_feed.postDelayed({
                    var scrollTo = rv_chat_feed.adapter.itemCount - 1
                    scrollTo = if (scrollTo >= 0) scrollTo else 0
                    rv_chat_feed.scrollToPosition(scrollTo)
                }, 10)
            }
        })

        rv_chat_feed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() < rv_chat_feed.adapter.itemCount - 5) {
                    btnScrollToEnd.isEnabled = true
                    btnScrollToEnd.visibility = View.VISIBLE
                } else {
                    btnScrollToEnd.isEnabled = false
                    btnScrollToEnd.visibility = View.GONE
                }
            }
        })
    }

    fun compensateTTSDelay() {
        Handler().post {
            textToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
                if (status != TextToSpeech.ERROR) {
                    val locale = textToSpeech?.getLanguage()
                    textToSpeech?.language = locale
                }
            })
        }
    }

    //Take user's speech as input and send the message
    override fun promptSpeechInput() {
        if ( recordingThread != null) {
            chatPresenter.stopHotwordDetection()
        }
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus.windowToken, 0)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.sttframe, STTfragment())
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
                ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
                        this@ChatActivity.packageName)
                textToSpeech?.language = Locale(language)
                textToSpeech?.speak(reply, TextToSpeech.QUEUE_FLUSH, ttsParams)
                audioFocus.abandonAudioFocus(afChangeListener)
            }
        }

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
                textToSpeech?.stop()
                chatPresenter.startSpeechInput()
            })
        } else {
            chatPresenter.check(false)
            btnSpeak.setImageResource(R.drawable.ic_send_fab)
            btnSpeak.setOnClickListener({
                val message = et_message.text.toString().trim({ it <= ' ' })
                if (!message.isEmpty()) {
                    chatPresenter.sendMessage(message, et_message.text.toString())
                    et_message.setText("")
                }
            })
        }
    }

    override fun checkEnterKeyPref(isChecked: Boolean) {
        if (isChecked) {
            et_message.imeOptions = EditorInfo.IME_ACTION_SEND
            et_message.inputType = InputType.TYPE_CLASS_TEXT
        } else {
            et_message.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
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
        rv_chat_feed.smoothScrollToPosition(rv_chat_feed.adapter.itemCount - 1)
    }

    fun openSettings(view: View) {
        val i = Intent(this, SkillsActivity::class.java)
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

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 0) {
            chatPresenter.exitChatActivity()
        } else {
            supportFragmentManager.popBackStackImmediate()
        }
    }

    override fun finishActivity() {
        finish()
    }

    override fun onResume() {
        super.onResume()
        chatPresenter.getUndeliveredMessages()

        if(!example.isEmpty()) {
            chatPresenter.addToNonDeliveredList(example, example)
            example = ""
        }

        registerReceiver(networkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )

        if (recordingThread != null)
            chatPresenter.startHotwordDetection()

        if (et_message.text.toString().isNotEmpty()) {
            btnSpeak.setImageResource(R.drawable.ic_send_fab)
            et_message.setText("")
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
}