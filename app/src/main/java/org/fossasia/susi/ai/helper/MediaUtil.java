package org.fossasia.susi.ai.helper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaRecorder;
import android.speech.RecognizerIntent;

/**
 * <h1>Helper class to check if STT and TTS is possible for phone.</h1>
 *
 * Created by chiragw15 on 6/6/17.
 */
public class MediaUtil {

    /**
     * returns whether a microphone exists
     *
     * @param context the context
     * @return the boolean
     */
    private static boolean getMicrophoneExists(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    /**
     * returns whether the microphone is available
     *
     * @param context the context
     * @return the boolean
     */
    private static boolean getMicrophoneAvailable(Context context) {
        MediaRecorder recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setOutputFile(new File(context.getCacheDir(), "MediaUtil#micAvailTestFile").getAbsolutePath());
        boolean available = true;
        try {
            recorder.prepare();
        } catch (IOException exception) {
            available = false;
        }
        recorder.release();
        return available;
    }

    /**
     * returns whether text to speech is available
     *
     * @param context the context
     * @return the boolean
     */
    private static boolean getTTSAvailable(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        List<ResolveInfo> speechActivities = packageManager.queryIntentActivities(speechIntent, 0);
        return (speechActivities.size() != 0);
    }

    /**
     * Is available for voice input boolean.
     *
     * @param context the context
     * @return the boolean
     */
    public static boolean isAvailableForVoiceInput(Context context) {
        return getMicrophoneExists(context) && getMicrophoneAvailable(context) && getTTSAvailable(context);
    }
}
