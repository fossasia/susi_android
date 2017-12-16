package org.fossasia.susi.ai.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.chat_bubble_layout.view.*
import org.fossasia.susi.ai.MainApplication
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.ChatModel
import org.fossasia.susi.ai.data.contract.IChatModel
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.LocationHelper
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.rest.responses.susi.SusiResponse
import retrofit2.Response
import java.util.*

/**
 * Created by yashladha on 14/12/17.
 */
class ChatBubbleService : Service() {

    private lateinit var mWindowManager: WindowManager
    private lateinit var mView: View
    private lateinit var chatBubble: FloatingActionButton
    private lateinit var rootLayout : LinearLayout
    private var initX: Int = 0
    private var initY: Int = 0
    private val chatModel = ChatModel()
    private var initTouchX: Float = 0.0f
    private var initTouchY: Float = 0.0f
    private lateinit var params: WindowManager.LayoutParams

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        Log.d("Chat bubble init", "Bubble initiated")

        setTheme(R.style.AppTheme)
        mView = LayoutInflater.from(this).inflate(R.layout.chat_bubble_layout, null)
        rootLayout = mView.expandable_chat
        rootLayout.visibility = View.GONE

        params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP and Gravity.START
        params.x = 0
        params.y = 100

        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWindowManager.addView(mView, params)

        chatBubble = mView.fab_chat_bubble
        chatBubble.isClickable = true

        chatBubble.performClick()

        chatBubble.setOnTouchListener(object: View.OnTouchListener {

            var time_start : Long = 0
            var time_end : Long = 0

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        time_start = System.currentTimeMillis()
                        initX = params.x
                        initY = params.y
                        initTouchX = event.rawX
                        initTouchY = event.rawY
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        val xDiff = event.rawX - initTouchX
                        val yDiff = event.rawY - initTouchY

                        // Condition for checking of the occurrence of click event
                        if (Math.abs(xDiff) < 5 && Math.abs(yDiff) < 5) {
                            time_end = System.currentTimeMillis()
                            if ((time_end - time_start) < 300) {
                                Clicked()
                                rootLayout.et_chat_widget_query.requestFocus()
                            }
                        }

                        params.x = initX + (xDiff).toInt()
                        params.y = initY + (yDiff).toInt()

                        mWindowManager.updateViewLayout(mView, params)
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        // Event for dragging of the floating widget on the screen
                        val xDiff = event.rawX - initTouchX
                        val yDiff = event.rawY - initTouchY
                        params.x = initX + xDiff.toInt()
                        params.y = initY + yDiff.toInt()
                        mWindowManager.updateViewLayout(mView, params)
                        return true
                    }
                }
                return false
            }
        })

        rootLayout.ibtn_chat_widget_submit.setOnClickListener {
            val query = rootLayout.et_chat_widget_query.text.toString().trim()
            rootLayout.et_chat_widget_query.setText("")
            // TODO: Query part of SUSI Android
            QuerySusi(query)
        }

        // Closing of the chat feed
        rootLayout.setOnLongClickListener {
            chatBubble.visibility = View.VISIBLE
            rootLayout.visibility = View.GONE
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            mWindowManager.updateViewLayout(mView, params)
            true
        }

    }

    private fun QuerySusi(query: String) {
        val locationHelper = LocationHelper(MainApplication.getInstance().applicationContext)
        locationHelper.getLocation()
        var latitude = 0.0
        var longitude = 0.0
        var source = ""
        if (locationHelper.canGetLocation()) {
            latitude = locationHelper.latitude
            longitude = locationHelper.longitude
            source = locationHelper.source
        }
        val tz = TimeZone.getDefault()
        val now = Date()
        val timezoneOffset = -1 * (tz.getOffset(now.time) / 60000)
        val language = if (PrefManager.getString(Constant.LANGUAGE, Constant.DEFAULT).equals(Constant.DEFAULT)) Locale.getDefault().language else PrefManager.getString(Constant.LANGUAGE, Constant.DEFAULT)

        chatModel.getSusiMessage(timezoneOffset, longitude, latitude, source, language, query, object: IChatModel.OnMessageFromSusiReceivedListener {
            override fun onSusiMessageReceivedSuccess(response: Response<SusiResponse>?) {
                if (response != null) {
                    val res = response.body()
                    if (res != null) {
                        Log.d("Response", res.toString())
                    }
                }
            }

            override fun onSusiMessageReceivedFailure(t: Throwable) {
                Log.e("Response", t.message)
            }
        })
    }

    // Click event on the floating widget
    private fun Clicked() {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        chatBubble.visibility = View.GONE
        rootLayout.visibility = View.VISIBLE
    }


    override fun onDestroy() {
        super.onDestroy()
        if (mView != null) {
            mWindowManager.removeView(mView)
        }
    }
}