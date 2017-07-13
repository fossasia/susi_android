package org.fossasia.susi.ai.chat

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.adapters.recycleradapters.ChatFeedRecyclerAdapter
import org.fossasia.susi.ai.data.model.ChatMessage
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.LocationHelper
import org.fossasia.susi.ai.helper.PrefManager

/**
 * Created by chiragw15 on 9/7/17.
 */
class ChatActivity: AppCompatActivity(), IChatView  {

    lateinit var chatPresenter: IChatPresenter
    val PERM_REQ_CODE = 1
    lateinit var toolbarImg: ImageView
    lateinit var recyclerAdapter: ChatFeedRecyclerAdapter
    //might want to remove it later
    var micCheck = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolbar()
        setEditText()

        val firstRun = intent.getBooleanExtra(Constant.FIRST_TIME, false)

        chatPresenter = ChatPresenter(this)
        chatPresenter.onAttach(this, applicationContext)
        chatPresenter.setUp()
        chatPresenter.retrieveOldMessages(firstRun)
        chatPresenter.getLocationFromIP()
        chatPresenter.getUndeliveredMessages()
        chatPresenter.initiateHotwordDetection()
        hideVoiceInput()
    }

    override fun onStart() {
        super.onStart()
        chatPresenter.getLocationFromLocationService(applicationContext)
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
                if (charSequence.toString().trim { it <= ' ' }.isNotEmpty() || !micCheck) {
                    btnSpeak.setImageResource(R.drawable.ic_send_fab)
                    btnSpeak.setOnClickListener ({
                        check = false
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
                        check = true
                        displayVoiceInput()
                        //promptSpeechInput()
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

        rv_chat_feed.setOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    override fun setChatBackground(previouslyChatImage: String) {
        val bg: Drawable
        if (previouslyChatImage.equals(getString(R.string.background_no_wall), ignoreCase = true)) {
            //set no wall
            window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.default_bg))
        } else if (!previouslyChatImage.equals("", ignoreCase = true)) {
            val b = Base64.decode(previouslyChatImage, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)
            bg = BitmapDrawable(resources, bitmap)
            //set Drawable bitmap which taking from gallery
            window.setBackgroundDrawable(bg)
        } else {
            //set default layout when app launch first time
            window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.default_bg))
        }
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

    override fun displayPartialSTT(text: String) {
        voice_input_text.setText(text)
    }

    override fun showVoiceDots() {
        dots.show()
        dots.start()
    }

    var check = false
    override fun checkMicPref(micCheck: Boolean) {
        if (micCheck) {
            btnSpeak.setImageResource(R.drawable.ic_mic_24dp)
            btnSpeak.setOnClickListener({
                check = true
                displayVoiceInput()
                //promptSpeechInput()
            })
        } else {
            check = false
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
                            //getLocationFromLocationService()
                            //locationHelper.getLocation()
                        }

                        Manifest.permission.RECORD_AUDIO -> {
                            if (grantResults.isNotEmpty() || grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                //micCheck = false
                                //PrefManager.putBoolean(Constant.MIC_INPUT, false)
                            } else {
                                //PrefManager.putBoolean(Constant.MIC_INPUT, PrefManager.checkMicInput(this))
                            }
                            audioPermissionGiven = true
                        }

                        Manifest.permission.WRITE_EXTERNAL_STORAGE -> if (grantResults.size >= 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED && audioPermissionGiven) {
                            if (Build.CPU_ABI.contains("arm") && !Build.FINGERPRINT.contains("generic") && PrefManager.checkMicInput(this))
                                //initHotword()
                            else {
                                //showToast(getString(R.string.error_hotword))
                                //PrefManager.putBoolean(Constant.HOTWORD_DETECTION, false)
                            }
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


    /*override fun showRetrieveOldMessageProgress() {
        val progressDialog = ProgressDialog(this@ChatActivity)
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.dialog_retrieve_messages_title))
        progressDialog.show()
    }*/

}