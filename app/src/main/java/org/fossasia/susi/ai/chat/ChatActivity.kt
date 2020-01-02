package org.fossasia.susi.ai.chat

import ai.kitt.snowboy.MsgEnum
import ai.kitt.snowboy.audio.AudioDataSaver
import ai.kitt.snowboy.audio.RecordingThread
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GestureDetectorCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import io.realm.RealmResults
import java.util.Locale
import kotlinx.android.synthetic.main.activity_chat.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.adapters.recycleradapters.ChatFeedRecyclerAdapter
import org.fossasia.susi.ai.chat.contract.IChatPresenter
import org.fossasia.susi.ai.chat.contract.IChatView
import org.fossasia.susi.ai.chat.search.ChatSearchActivity
import org.fossasia.susi.ai.data.model.ChatMessage
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.helper.Utils.hideSoftKeyboard
import org.fossasia.susi.ai.skills.SkillsActivity
import timber.log.Timber

/**
 * The Chat Activity. Does all the main processes including
 * getting location of user, searching, interacting with susi, Hotword Detection, STT and TTS
 *
 * The V in MVP
 * Created by chiragw15 on 9/7/17.
 */
@Suppress("UNUSED_PARAMETER", "DEPRECATION")
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
    private var gestureDetectorCompat: GestureDetectorCompat? = null
    private var isConfigurationChanged = false
    private var isSearching: Boolean = false
    private val voiceSearchCode = 10
    private val enterAsSend: Boolean by lazy {
        PrefManager.getBoolean(R.string.settings_enterPreference_key, false)
    }

    private val changeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            textToSpeech?.stop()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        isSearching = true

        val firstRun = intent.getBooleanExtra(Constant.FIRST_TIME, false)
        gestureDetectorCompat = GestureDetectorCompat(this, CustomGestureListener())

        chatPresenter = ChatPresenter(this)
        chatPresenter.onAttach(this)

        youtubeVid = YoutubeVid(this)
        setUpUI()
        initializationMethod(firstRun)

        if (intent.hasExtra("example")) {
            example = intent.getStringExtra("example")
            intent.removeExtra("example")
        }

        networkStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                chatPresenter.startComputingThread()
            }
        }
        searchChat.visibility = View.VISIBLE
        fabsetting.visibility = View.VISIBLE
        voiceSearchChat.visibility = View.VISIBLE
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        this.gestureDetectorCompat?.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    // Inner class for handling the gestures
    internal inner class CustomGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onFling(
            event1: MotionEvent?,
            event2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (event1 == null || event2 == null)
                return false
            val X = event1.getX() - event2.getX()
            // Swipe from right to left
            if (X >= 100 && X <= 1000) {
                val intent = Intent(this@ChatActivity, SkillsActivity::class.java)
                startActivity(intent)
            }
            return true
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

    fun openChatSearch(view: View) {
        val interpolator = OvershootInterpolator()
        if (isSearching == false || chatSearchInput.getVisibility() == View.VISIBLE) {
            ViewCompat.animate(view)
                    .rotation(0f)
                    .withLayer()
                    .setDuration(300)
                    .setInterpolator(interpolator)
                    .start()
            chatSearchInput.setVisibility(View.GONE)
            hideSoftKeyboard(this, window.decorView)
        } else {
            ViewCompat.animate(view)
                    .rotation(90f)
                    .withLayer()
                    .setDuration(300)
                    .setInterpolator(interpolator)
                    .start()
            chatSearchInput.setVisibility(View.VISIBLE)
            handleSearch()
        }
    }

    fun openVoiceSearch(view: View) {
        chatPresenter.stopHotwordDetection()
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, voiceSearchCode) // Sends the detected query to search
        } else {
            Toast.makeText(this, R.string.error_voice_chat_search, Toast.LENGTH_SHORT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            voiceSearchCode -> if (resultCode == Activity.RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                performSearch(result[0])
            }
        }
    }

    fun handleSearch() {
        chatSearchInput?.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP && chatSearchInput?.text.toString().isNotEmpty()) {
                performSearch(chatSearchInput?.text.toString())
            }
            false
        }
        chatSearchInput?.requestFocus()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(chatSearchInput, InputMethodManager.SHOW_IMPLICIT)
        isSearching = true
    }

    fun performSearch(query: String): Boolean {
        var intent = Intent(this, ChatSearchActivity::class.java)
        intent.putExtra("query", query)
        startActivity(intent)
        return true
    }

    override fun onBackPressed() {
        if (isSearching == true) {
            chatSearchInput.clearFocus()
            chatSearchInput.setVisibility(View.INVISIBLE)
            hideSoftKeyboard(this, window.decorView)
            super.onBackPressed()
        }
    }

    private fun setEditText() {
        val watch = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                // do whatever you want to do before text change in input edit text
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.toString().trim { it <= ' ' }.isNotEmpty() || !chatPresenter.micCheck()) {
                    btnSpeak.setImageResource(R.drawable.ic_send_fab)
                    btnSpeak.setOnClickListener {
                        chatPresenter.check(false)
                        val chatMessage = askSusiMessage.text.toString().trim { it <= ' ' }
                        val splits = chatMessage.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
                        val message = splits.joinToString(" ")
                        if (!chatMessage.isEmpty()) {
                            chatPresenter.sendMessage(message, askSusiMessage.text.toString())
                            askSusiMessage.setText("")
                        }
                    }
                } else {
                    btnSpeak.setImageResource(R.drawable.ic_mic_24dp)
                    btnSpeak.setOnClickListener {
                        textToSpeech?.stop()
                        chatPresenter.startSpeechInput()
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {
                // do whatever you want to do after text change in input edit text
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
                val message = askSusiMessage.text.toString().trim { it <= ' ' }
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
                val message = askSusiMessage.text.toString().trim { it <= ' ' }
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

    // Take user's speech as input and send the message
    override fun promptSpeechInput() {
        if (recordingThread != null) {
            chatPresenter.stopHotwordDetection()
        }
        hideSoftKeyboard(this, window.decorView)
        searchChat.visibility = View.GONE
        fabsetting.visibility = View.GONE
        voiceSearchChat.visibility = View.GONE
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.speechToTextFrame, STTFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    // Replies user with Speech
    override fun voiceReply(reply: String, language: String) {
        searchChat.visibility = View.VISIBLE
        fabsetting.visibility = View.VISIBLE
        voiceSearchChat.visibility = View.VISIBLE
        val audioFocus = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val handler = Handler()
        handler.post {
            val result = audioFocus.requestAudioFocus(changeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
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

                    @Suppress("OverridingDeprecatedMember")
                    override fun onError(s: String) {
                        if (recordingThread != null)
                            chatPresenter.startHotwordDetection()
                    }
                })

                val ttsParams = HashMap<String, String>()
                ttsParams[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = this@ChatActivity.packageName
                textToSpeech?.language = Locale(language)
                textToSpeech?.speak(reply, TextToSpeech.QUEUE_FLUSH, ttsParams)
                audioFocus.abandonAudioFocus(changeListener)
            }
        }
        startActivity(getIntent())
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
        recyclerAdapter.notifyDataSetChanged()
    }

    override fun displaySnackbar(message: String) {
        val snackbar = Snackbar.make(coordinator_layout, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    // Initiates hotword detection
    override fun initHotword() {
        recordingThread = RecordingThread(object : Handler() {
            override fun handleMessage(msg: Message) {
                val message = MsgEnum.getMsgEnum(msg.what)
                when (message) {
                    MsgEnum.MSG_ACTIVE -> {
                        chatPresenter.hotwordDetected()
                    }
                    MsgEnum.MSG_INFO -> Unit
                    MsgEnum.MSG_VAD_SPEECH -> Unit
                    MsgEnum.MSG_VAD_NOSPEECH -> Unit
                    MsgEnum.MSG_ERROR -> Unit
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
            btnSpeak.setOnClickListener {
                btnSpeak.isEnabled = false
                textToSpeech?.stop()
                chatPresenter.startSpeechInput()
            }
        } else {
            chatPresenter.check(false)
            btnSpeak.setImageResource(R.drawable.ic_send_fab)
            btnSpeak.setOnClickListener {
                val message = askSusiMessage.text.toString().trim { it <= ' ' }
                if (!message.isEmpty()) {
                    chatPresenter.sendMessage(message, askSusiMessage.text.toString())
                    askSusiMessage.setText("")
                }
            }
        }
    }

    override fun checkEnterKeyPref(isChecked: Boolean) {
        if (isChecked) {
            askSusiMessage.imeOptions = EditorInfo.IME_ACTION_SEND
            askSusiMessage.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD // setting this programmatically works for all devices
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        when (requestCode) {
            PERM_REQ_CODE -> run {
                var audioPermissionGiven = false
                for ((index, permission) in permissions.withIndex()) {
                    if (permission == null)
                        continue
                    val granted = grantResults.isNotEmpty() && grantResults[index] == PackageManager.PERMISSION_GRANTED
                    when (permission) {
                        Manifest.permission.ACCESS_FINE_LOCATION -> if (granted) {
                            chatPresenter.getLocationFromLocationService()
                        }

                        Manifest.permission.RECORD_AUDIO -> {
                            chatPresenter.disableMicInput(!granted)
                            audioPermissionGiven = granted
                        }

                        Manifest.permission.WRITE_EXTERNAL_STORAGE -> if (granted && audioPermissionGiven) {
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

    fun openSettings(view: View) {
        val intent = Intent(this, SkillsActivity::class.java)
        startActivity(intent)
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
            Timber.d("Message is not empty.")
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

    companion object {
        val ALARM = "ALARM"
    }
}
