package org.fossasia.susi.ai.chat

import android.annotation.TargetApi
import android.app.PictureInPictureParams
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION_CODES
import android.widget.ScrollView
import android.widget.VideoView
import androidx.annotation.RequiresApi
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.PrefManager.getString

// implement pip here
class YoutubeVid(val context: Context) : IYoutubeVid {

    companion object {

        /** Intent action for media controls from Picture-in-Picture mode.  */
        private const val ACTION_MEDIA_CONTROL = "media_control"

        /** Intent extra for media controls from Picture-in-Picture mode.  */
        private const val EXTRA_CONTROL_TYPE = "control_type"

        /** The request code for play action PendingIntent.  */
        private const val REQUEST_PLAY = 1

        /** The request code for pause action PendingIntent.  */
        private const val REQUEST_PAUSE = 2

        /** The request code for info action PendingIntent.  */
        private const val REQUEST_INFO = 3

        /** The intent extra value for play action.  */
        private const val CONTROL_TYPE_PLAY = 1

        /** The intent extra value for pause action.  */
        private const val CONTROL_TYPE_PAUSE = 2
    }

    /** The arguments to be used for Picture-in-Picture mode.  */
    @RequiresApi(api = VERSION_CODES.O)
    @TargetApi(VERSION_CODES.O)
    private val mPictureInPictureParamsBuilder = PictureInPictureParams.Builder()
    /** This shows the video.  */
    private lateinit var mMovieView: VideoView
    private lateinit var mScrollView: ScrollView
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                if (intent.action != ACTION_MEDIA_CONTROL) {
                    return
                }

                // This is where we are called back from Picture-in-Picture action items.
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
            updatePictureInPictureActions(R.drawable.ic_pause_24dp, labelPause,
                    CONTROL_TYPE_PAUSE, REQUEST_PAUSE)
        }

        override fun onMovieStopped() {
            // The video stopped or reached its end. In PiP mode, we want to show an action item
            // to play the video.
            updatePictureInPictureActions(R.drawable.ic_play_arrow_24dp, labelPlay,
                    CONTROL_TYPE_PLAY, REQUEST_PLAY)
        }

        override fun onMovieMinimized() {
            minimize()
        }
    }

    override fun playYoutubeVid(videoId: String) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
