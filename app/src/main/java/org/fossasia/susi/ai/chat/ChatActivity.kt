package org.fossasia.susi.ai.chat

import ai.kitt.snowboy.MsgEnum
import ai.kitt.snowboy.audio.AudioDataSaver
import ai.kitt.snowboy.audio.RecordingThread

import android.Manifest
import android.app.ProgressDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.*
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

import io.realm.RealmResults

import kotlinx.android.synthetic.main.activity_chat.*

import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.adapters.recycleradapters.ChatFeedRecyclerAdapter
import org.fossasia.susi.ai.chat.contract.IChatPresenter
import org.fossasia.susi.ai.chat.contract.IChatView
import org.fossasia.susi.ai.data.model.ChatMessage
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.ImageUtils
import org.fossasia.susi.ai.login.LoginActivity
import org.fossasia.susi.ai.settings.SettingsActivity

import java.io.FileNotFoundException
import java.util.HashMap

/**
 * The Chat Activity. Does all the main processes including
 * getting location of user, searching, interacting with susi, Hotword Detection, STT and TTS
 *
 * The V in MVP
 * Created by chiragw15 on 9/7/17.
 */
class ChatActivity: AppCompatActivity(), IChatView {

    val TAG: String = ChatActivity::class.java.name

    val SELECT_PICTURE = 200
    val CROP_PICTURE = 400
    lateinit var chatPresenter: IChatPresenter
    val PERM_REQ_CODE = 1
    lateinit var toolbarImg: ImageView
    lateinit var recyclerAdapter: ChatFeedRecyclerAdapter
    lateinit var textToSpeech: TextToSpeech
    var recordingThread: RecordingThread? = null
    lateinit var networkStateReceiver: BroadcastReceiver
    lateinit var recognizer: SpeechRecognizer
    lateinit var searchView: SearchView
    lateinit var menu: Menu
    lateinit var progressDialog: ProgressDialog

    val afChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            textToSpeech.stop()
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
        cancelSpeechInput()

        networkStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                chatPresenter.startComputingThread()
            }
        }
        registerReceiver(networkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        recognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)
    }

    // This method is used to set up the UI components
    // of Chat Activity like Adapter, Toolbar, Background, Theme, etc
    fun setUpUI() {
        chatPresenter.setUp()
        chatPresenter.checkPreferences()
        chatPresenter.setUpBackground()
        setToolbar()
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
        hideVoiceInput()
    }

    fun setToolbar() {
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.toolbar)
        supportActionBar?.title = ""
        toolbarImg = supportActionBar?.customView?.findViewById(R.id.toolbar_img) as ImageView
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

    override fun setTheme(darkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode( if(darkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun setChatBackground(bg: Drawable?) {
        if(bg == null) {
            window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.default_bg))
        } else {
            window.setBackgroundDrawable(bg)
        }
    }

    fun compensateTTSDelay() {
        Handler().post {
            textToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
                if (status != TextToSpeech.ERROR) {
                    val locale = textToSpeech.getLanguage()
                    textToSpeech.language = locale
                }
            })
        }
    }

    //Take user's speech as input and send the message
    override fun promptSpeechInput() {
        if(recordingThread != null)
            chatPresenter.stopHotwordDetection()
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                "com.domain.app")
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

        val listener = object : RecognitionListener {
            override fun onResults(results: Bundle) {
                val voiceResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (voiceResults == null) {
                    Log.e(TAG, "No voice results")
                } else {
                    Log.d(TAG, "Printing matches: ")
                    for (match in voiceResults) {
                        Log.d(TAG, match)
                    }
                }
                chatPresenter.sendMessage(voiceResults[0], voiceResults[0])
                recognizer.destroy()
                hideVoiceInput()
                if(recordingThread != null)
                    chatPresenter.startHotwordDetection()
            }

            override fun onReadyForSpeech(params: Bundle) {
                Log.d(TAG, "Ready for speech")
                showVoiceDots()
            }

            override fun onError(error: Int) {
                Log.d(TAG, "Error listening for speech: " + error)
                showToast("Could not recognize speech, try again.")
                recognizer.destroy()
                hideVoiceInput()
                if(recordingThread != null)
                chatPresenter.startHotwordDetection()
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
                displayPartialSTT(partial[0])
            }

            override fun onRmsChanged(rmsdB: Float) {
                // This method is intentionally empty
            }
        }
        recognizer.setRecognitionListener(listener)
        recognizer.startListening(intent)
    }

    //Replies user with Speech
     override fun voiceReply(reply: String) {
        val audioFocus = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val handler = Handler()
        handler.post {
            val result = audioFocus.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
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
                textToSpeech.speak(reply, TextToSpeech.QUEUE_FLUSH, ttsParams)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val mHandler = Handler(Looper.getMainLooper())
        when (requestCode) {
            CROP_PICTURE -> {
                if (resultCode == RESULT_OK && null != data) {
                    mHandler.post {
                        try {
                            val thePic = data.extras.getParcelable<Bitmap>("data")
                            val encodedImage = ImageUtils.Companion.cropImage(thePic)
                            chatPresenter.cropPicture(encodedImage)
                        } catch (e: NullPointerException) {
                            Log.d(TAG, e.localizedMessage)
                        }
                    }
                }
            }
            SELECT_PICTURE -> {
                if (resultCode == RESULT_OK && null != data) {
                    mHandler.post {
                        val selectedImageUri = data.data
                        try {
                            cropCapturedImage(ImageUtils.Companion.getImageUrl(applicationContext, selectedImageUri))
                        } catch (aNFE: ActivityNotFoundException) {
                            //display an error message if user device doesn't support
                            showToast(getString(R.string.error_crop_not_supported))
                            try {
                                chatPresenter.cropPicture(ImageUtils.Companion.encodeImage(applicationContext,selectedImageUri))
                            } catch (e: FileNotFoundException) {
                                e.printStackTrace()
                            }

                        }
                    }
                }
            }
        }
    }

    fun cropCapturedImage(picUri: Uri?) {
        val cropIntent = Intent("com.android.camera.action.CROP")
        cropIntent.setDataAndType(picUri, "image/*")
        cropIntent.putExtra("crop", "true")
        cropIntent.putExtra("aspectX", 9)
        cropIntent.putExtra("aspectY", 14)
        cropIntent.putExtra("outputX", 256)
        cropIntent.putExtra("outputY", 256)
        cropIntent.putExtra("return-data", true)
        startActivityForResult(cropIntent, CROP_PICTURE)
    }

    override fun hideVoiceInput() {
        voice_input_text.text = ""
        voice_input_text.visibility = View.GONE
        cancel.visibility = View.GONE
        dots.hideAndStop()
        dots.visibility = View.GONE
        et_message.visibility = View.VISIBLE
        btnSpeak.visibility = View.VISIBLE
        et_message.requestFocus()
    }

    override fun displayVoiceInput() {
        dots.visibility = View.VISIBLE
        voice_input_text.visibility = View.VISIBLE
        cancel.visibility = View.VISIBLE
        et_message.visibility = View.GONE
        btnSpeak.visibility = View.GONE
    }

    fun cancelSpeechInput() {
        cancel.setOnClickListener {
            recognizer.cancel()
            recognizer.destroy()
            hideVoiceInput()
            if (recordingThread != null)
                chatPresenter.startHotwordDetection()
        }
    }

    override fun displayPartialSTT(text: String) {
        voice_input_text.text = text
    }

    override fun showVoiceDots() {
        dots.show()
        dots.start()
    }

    override fun checkMicPref(micCheck: Boolean) {
        if (micCheck) {
            chatPresenter.check(true)
            btnSpeak.setImageResource(R.drawable.ic_mic_24dp)
            btnSpeak.setOnClickListener({
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

    override fun showRetrieveOldMessageProgress() {
        progressDialog = ProgressDialog(this@ChatActivity)
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.dialog_retrieve_messages_title))
        progressDialog.show()
    }

    override fun hideRetrieveOldMessageProgress() {
        progressDialog.dismiss()
    }

    override fun enableLoginInMenu(isVisible: Boolean) {
        menu.findItem(R.id.action_logout).isVisible = !isVisible
        menu.findItem(R.id.action_login).isVisible = isVisible
    }

    override fun displaySearchElements(isSearchEnabled: Boolean) {
        toolbarImg.visibility = if(isSearchEnabled) View.GONE else View.VISIBLE
        et_message.visibility = if(isSearchEnabled) View.GONE else View.VISIBLE
        btnSpeak.visibility = if(isSearchEnabled) View.GONE else View.VISIBLE
        send_message_layout.visibility = if(isSearchEnabled) View.GONE else View.VISIBLE
    }

    override fun modifyMenu(show: Boolean) {
        menu.findItem(R.id.up_angle).isVisible = show
        menu.findItem(R.id.down_angle).isVisible = show
    }

    override fun searchMovement(position: Int) {
        rv_chat_feed.scrollToPosition(position)
        recyclerAdapter.highlightMessagePosition = position
        recyclerAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_main, menu)
        chatPresenter.onMenuCreated()

        searchView = MenuItemCompat.getActionView(menu.findItem(R.id.action_search)) as SearchView
        val editText = searchView.findViewById(R.id.search_src_text)

        searchView.setOnSearchClickListener({
            chatPresenter.startSearch()
        })

        searchView.setOnCloseListener({
            recyclerAdapter.highlightMessagePosition = -1
            recyclerAdapter.notifyDataSetChanged()
            searchView.onActionViewCollapsed()
            chatPresenter.stopSearch()
            false
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                //Handle Search Query
                chatPresenter.onSearchQuerySearched(query)
                recyclerAdapter.query = query
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (TextUtils.isEmpty(newText)) {
                    modifyMenu(false)
                    recyclerAdapter.highlightMessagePosition = -1
                    recyclerAdapter.notifyDataSetChanged()
                    if (!editText.isFocused) {
                        editText.requestFocus()
                    }
                } else {
                    chatPresenter.onSearchQuerySearched(newText)
                    recyclerAdapter.query = newText
                }
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val i = Intent(this, SettingsActivity::class.java)
                startActivity(i)
                return true
            }
            R.id.wall_settings -> {
                selectBackground()
                return true
            }
            R.id.action_share -> {
                try {
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_TEXT,
                            String.format(getString(R.string.promo_msg_template),
                                    String.format(getString(R.string.app_share_url), packageName)))
                    startActivity(shareIntent)
                } catch (e: Exception) {
                    showToast(getString(R.string.error_msg_retry))
                }

                return true
            }
            R.id.up_angle -> {
                chatPresenter.searchUP()
                return true
            }
            R.id.down_angle -> {
                chatPresenter.searchDown()
                return true
            }
            R.id.action_logout -> {
                chatPresenter.setLogoutDialog()
                return true
            }
            R.id.action_login -> {
                chatPresenter.login()
                return false
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setLogoutDialog(darkTheme: Boolean) {
        val d = AlertDialog.Builder(this)
        d.setMessage("Are you sure ?").setCancelable(false).setPositiveButton("Yes") { _, _ ->
            chatPresenter.logout()
        }.setNegativeButton("No") { dialog, _ -> dialog.cancel() }

        val alert = d.create()
        alert.setTitle(getString(R.string.logout))
        alert.show()
        val nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        if(darkTheme) {
            nbutton.setTextColor(Color.WHITE)
            pbutton.setTextColor(Color.WHITE)
        }
        else {
            nbutton.setTextColor(Color.BLUE)
            pbutton.setTextColor(Color.BLUE)
        }
    }

    fun selectBackground() {
        val builder = AlertDialog.Builder(this@ChatActivity)
        builder.setTitle(R.string.dialog_action_complete)
        builder.setItems(R.array.dialog_complete_action_items) { _, which ->
            chatPresenter.openSelectBackgroundDialog(which)
        }
        builder.create().show()
    }

    override fun openImagePickerActivity() {
        val i = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, SELECT_PICTURE)
    }

    override fun startLoginActivity() {
        val intent = Intent(this@ChatActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (!searchView.isIconified) {

            chatPresenter.stopSearch()
            recyclerAdapter.highlightMessagePosition = -1
            recyclerAdapter.notifyDataSetChanged()
            searchView.onActionViewCollapsed()
            recyclerAdapter.clearSelection()

            return
        }
        chatPresenter.exitChatActivity()
    }

    override fun finishActivity() {
        finish()
    }

    override fun onResume() {
        super.onResume()

        chatPresenter.getUndeliveredMessages()

        registerReceiver(networkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )

        chatPresenter.checkPreferences()

        if (recordingThread != null)
            chatPresenter.startHotwordDetection()

        if (et_message.text.toString().isNotEmpty()) {
            btnSpeak.setImageResource(R.drawable.ic_send_fab)
        }
    }

    override fun onPause() {
        unregisterReceiver(networkStateReceiver)
        super.onPause()
        recognizer.cancel()
        recognizer.destroy()

        hideVoiceInput()

        if (recordingThread != null)
            chatPresenter.stopHotwordDetection()

    }

    override fun onDestroy() {
        super.onDestroy()
        rv_chat_feed.clearOnScrollListeners()

        textToSpeech.shutdown()
        chatPresenter.onDetach()
    }
}