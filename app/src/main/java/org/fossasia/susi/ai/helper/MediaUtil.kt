package org.fossasia.susi.ai.helper

import java.io.File
import java.io.IOException

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.media.MediaRecorder
import android.speech.RecognizerIntent
import android.support.v4.app.ActivityCompat

/**
 * <h1>Helper class to check if STT and TTS is possible for phone.</h1>

 * Created by chiragw15 on 6/6/17.
 */
object MediaUtil {

    /**
     * returns whether a microphone exists

     * @param context the context
     * *
     * @return the boolean
     */
    private fun getMicrophoneExists(context: Context): Boolean {
        val packageManager = context.packageManager
        return packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
    }

    /**
     * returns whether the microphone is available

     * @param context the context
     * *
     * @return the boolean
     */
    private fun getMicrophoneAvailable(context: Context): Boolean {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        val recorder = MediaRecorder()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
        recorder.setOutputFile(File(context.cacheDir, "MediaUtil#micAvailTestFile").absolutePath)
        var available = true
        try {
            recorder.prepare()
        } catch (exception: IOException) {
            available = false
        }

        recorder.release()
        return available
    }

    /**
     * returns whether text to speech is available

     * @param context the context
     * *
     * @return the boolean
     */
    private fun getTTSAvailable(context: Context): Boolean {
        val packageManager = context.packageManager
        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        val speechActivities = packageManager.queryIntentActivities(speechIntent, 0)
        return speechActivities.size != 0
    }

    /**
     * Is available for voice input boolean.

     * @param context the context
     * *
     * @return the boolean
     */
    fun isAvailableForVoiceInput(context: Context): Boolean {
        return getMicrophoneExists(context) && getMicrophoneAvailable(context) && getTTSAvailable(context)
    }
}
