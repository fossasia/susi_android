package org.fossasia.susi.ai.chat

import android.annotation.TargetApi
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Rational
import android.widget.Button
import android.widget.ScrollView
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.PrefManager.getString
import java.util.*

// implement pip here
class YoutubeVid(val context: Context) : IYoutubeVid {

    companion object {
        private const val ACTION_MEDIA_CONTROL = "media_control"
        private const val EXTRA_CONTROL_TYPE = "control_type"
        private const val REQUEST_PLAY = 1
        private const val REQUEST_PAUSE = 2
        private const val REQUEST_INFO = 3
        private const val CONTROL_TYPE_PLAY = 1
        private const val CONTROL_TYPE_PAUSE = 2
    }

    /** The arguments to be used for Picture-in-Picture mode.  */
    @RequiresApi(api = VERSION_CODES.O)
    @TargetApi(VERSION_CODES.O)
    private val mPictureInPictureParamsBuilder = PictureInPictureParams.Builder()
    /** this@YoutubeVid shows the video.  */
    private lateinit var mMovieView: MovieView
    private lateinit var mScrollView: ScrollView
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                if (intent.action != ACTION_MEDIA_CONTROL) {
                    return
                }

                // this@YoutubeVid is where we are called back from Picture-in-Picture action items.
                when (intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)) {
                    CONTROL_TYPE_PLAY -> mMovieView.play()
                    CONTROL_TYPE_PAUSE -> mMovieView.pause()
                }
            }
        }
    }

    private val labelPlay: String by lazy { getString(R.string.play) }
    private val labelPause: String by lazy { getString(R.string.pause) }

    /**
     * Callbacks from the [MovieView] showing the video playback.
     */
    private val mMovieListener = object : MovieView.MovieListener() {

        override fun onMovieStarted() {
            // We are playing the video now. In PiP mode, we want to show an action item to pause
            // the video.
            updatePictureInPictureActions(R.drawable.ic_pause_white_24dp, labelPause,
                    CONTROL_TYPE_PAUSE, REQUEST_PAUSE)
        }

        override fun onMovieStopped() {
            // The video stopped or reached its end. In PiP mode, we want to show an action item
            // to play the video.
            updatePictureInPictureActions(R.drawable.ic_play_arrow_white_24dp, labelPlay,
                    CONTROL_TYPE_PLAY, REQUEST_PLAY)
        }

        override fun onMovieMinimized() {
            minimize()
        }
    }

    internal fun updatePictureInPictureActions(@DrawableRes iconId: Int, title: String,
                                               controlType: Int, requestCode: Int) {
        val actions = ArrayList<RemoteAction>()


        mPictureInPictureParamsBuilder.setActions(actions)

        setPictureInPictureParams(mPictureInPictureParamsBuilder.build())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // View references
        mMovieView = findViewById(R.id.movie)
        mScrollView = findViewById(R.id.scroll)

        val switchExampleButton = findViewById<Button>(R.id.switch_example)
        switchExampleButton.text = getString(R.string.switch_media_session)
        switchExampleButton.setOnClickListener(SwitchActivityOnClick())

        // Set up the video; it automatically starts. This is the important portion here fun begins, so take care
        mMovieView.setMovieListener(mMovieListener)
        findViewById<Button>(R.id.pip).setOnClickListener { minimize() }
    }

    override fun onStop() {
        // On entering Picture-in-Picture mode, onPause is called, but not onStop.
        // For this reason, this is the place where we should pause the video playback.
        mMovieView.pause()
        super.onStop()
    }

    override fun onRestart() {
        super.onRestart()
        // Show the video controls so the video can be easily resumed.
        if (!isInPictureInPictureMode) {
            mMovieView.showControls()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        adjustFullScreen(newConfig)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            adjustFullScreen(resources.configuration)
        }
    }

    override fun onPictureInPictureModeChanged(
            isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            // Starts receiving events from action items in PiP mode.
            registerReceiver(mReceiver, IntentFilter(ACTION_MEDIA_CONTROL))
        } else {
            // We are out of PiP mode. We can stop receiving events from it.
            unregisterReceiver(mReceiver)
            // Show the video controls if the video is not playing
            if (!mMovieView.isPlaying) {
                mMovieView.showControls()
            }
        }
    }

    /**
     * Enters Picture-in-Picture mode.
     */
    internal fun minimize() {
        // Hide the controls in picture-in-picture mode.
        mMovieView.hideControls()
        // Calculate the aspect ratio of the PiP screen.
        mPictureInPictureParamsBuilder.setAspectRatio(Rational(mMovieView.width, mMovieView.height))
        enterPictureInPictureMode(mPictureInPictureParamsBuilder.build())
    }
    override fun playYoutubeVid(videoId: String) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
