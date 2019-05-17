package org.fossasia.susi.ai.chat

import android.app.ActionBar
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.Constant
import timber.log.Timber

/**
 *
 * This activity plays youtube video in the app, the video ID is received from the server.
 *
 * Created by batbrain7 on 16/06/2018
 *
 */

private const val YOUTUBE_KEY = "com.google.android.youtube.API_KEY"
private const val yourApiKey = "YOUR_API_KEY"

class YouTubeActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {
    private val RECOVERY_REQUEST = 1
    private lateinit var playerStateChangeListener: MyPlayerStateChangeListener
    private lateinit var playbackEventListener: MyPlaybackEventListener
    private var youtubeId: String = ""
    private lateinit var playerView: YouTubePlayerView
    private var apikey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.youtube_player)

        apikey = this.packageManager.getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA)
                .metaData.getString(YOUTUBE_KEY)

        if (yourApiKey.equals(apikey)) {
            Toast.makeText(applicationContext, applicationContext.getString(org.fossasia.susi.ai.R.string.api_key_not_set), Toast.LENGTH_LONG).show()
            finish()
        } else {
            init()
        }
    }

    fun init() {
        playerView = findViewById(R.id.youtubePlayer)
        playerView.initialize(apikey, this)
        playerStateChangeListener = MyPlayerStateChangeListener()
        playbackEventListener = MyPlaybackEventListener()

        this.window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

        val intent: Intent = getIntent()
        youtubeId = intent.getStringExtra(Constant.VIDEO_ID)
        Timber.d("onCreate() " + youtubeId)
    }

    override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, p1: YouTubePlayer?, p2: Boolean) {
        p1?.setPlayerStateChangeListener(playerStateChangeListener)
        p1?.setPlaybackEventListener(playbackEventListener)
        if (!p2 && youtubeId != null) {
            p1?.loadVideo(youtubeId)
        }
    }

    override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
        if (p1?.isUserRecoverableError()!!) {
            p1.getErrorDialog(this, RECOVERY_REQUEST).show()
        } else {
            val error = String.format(getString(R.string.player_error), p1.toString())
            Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            if (yourApiKey.equals(apikey)) {
                Toast.makeText(applicationContext, applicationContext.getString(org.fossasia.susi.ai.R.string.api_key_not_set), Toast.LENGTH_LONG).show()
                finish()
            } else {
                playerView.initialize(apikey, this)
            }
        }
    }

    class MyPlaybackEventListener : YouTubePlayer.PlaybackEventListener {
        override fun onSeekTo(p0: Int) {
            // To change body of created functions use File | Settings | File Templates.
        }

        override fun onBuffering(p0: Boolean) {
            // To change body of created functions use File | Settings | File Templates.
        }

        override fun onPlaying() {
            // To change body of created functions use File | Settings | File Templates.
        }

        override fun onStopped() {
            // To change body of created functions use File | Settings | File Templates.
        }

        override fun onPaused() {
            // To change body of created functions use File | Settings | File Templates.
        }
    }

    class MyPlayerStateChangeListener : YouTubePlayer.PlayerStateChangeListener {
        override fun onAdStarted() {
            // To change body of created functions use File | Settings | File Templates.
        }

        override fun onLoading() {
            // To change body of created functions use File | Settings | File Templates.
        }

        override fun onVideoStarted() {
            // To change body of created functions use File | Settings | File Templates.
        }

        override fun onLoaded(p0: String?) {
            // To change body of created functions use File | Settings | File Templates.
        }

        override fun onVideoEnded() {
            // To change body of created functions use File | Settings | File Templates.
        }

        override fun onError(p0: YouTubePlayer.ErrorReason?) {
            // To change body of created functions use File | Settings | File Templates.
        }
    }
}
