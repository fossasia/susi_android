package org.fossasia.susi.ai.data

import android.Manifest
import ai.kitt.snowboy.AppResCopy
import ai.kitt.snowboy.Constants
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Base64
import org.fossasia.susi.ai.data.contract.IUtilModel
import org.fossasia.susi.ai.helper.*
import org.fossasia.susi.ai.rest.responses.susi.LoginResponse
import retrofit2.Response
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

/**
 * Util Model class used for utilities like preferences, etc
 *
 * Created by chiragw15 on 10/7/17.
 */

class UtilModel(val context: Context): IUtilModel {

    override fun saveToken(response: Response<LoginResponse>) {
        PrefManager.putString(Constant.ACCESS_TOKEN, response.body().accessToken as String)
        val validity = System.currentTimeMillis() + response.body().validSeconds * 1000
        PrefManager.putLong(Constant.TOKEN_VALIDITY, validity)
    }

    override fun saveAnonymity(isAnonymous: Boolean) {
        PrefManager.putBoolean(Constant.ANONYMOUS_LOGGED_IN, isAnonymous)
    }

    override fun getAnonymity(): Boolean {
        return PrefManager.getBoolean(Constant.ANONYMOUS_LOGGED_IN, false)
    }

    override fun saveEmail(email: String) {
        val savedEmails = mutableSetOf<String>()
        if (PrefManager.getStringSet(Constant.SAVED_EMAIL) != null)
            savedEmails.addAll(PrefManager.getStringSet(Constant.SAVED_EMAIL))
        savedEmails.add(email)
        PrefManager.putString(Constant.SAVE_EMAIL, email)
        PrefManager.putStringSet(Constant.SAVED_EMAIL, savedEmails)
    }

    override fun getSavedEmails(): MutableSet<String>? {
        return PrefManager.getStringSet(Constant.SAVED_EMAIL)
    }

    override fun isLoggedIn(): Boolean {
        return !PrefManager.hasTokenExpired()
    }

    override fun clearToken() {
        PrefManager.clearToken()
    }

    override fun setServer(isSusiServer: Boolean) {
        PrefManager.putBoolean(Constant.SUSI_SERVER, isSusiServer)
    }

    override fun setCustomURL(url: String) {
        PrefManager.putString(Constant.CUSTOM_SERVER, CredentialHelper.getValidURL(url) as String)
    }

    override fun getString(id: Int): String {
        return context.getString(id)
    }

    override fun getBooleanPref(prefName: String, defaultValue: Boolean): Boolean {
        return PrefManager.getBoolean(prefName, defaultValue);
    }

    override fun putBooleanPref(prefName: String, value: Boolean) {
        PrefManager.putBoolean(prefName, value)
    }

    override fun checkMicInput(): Boolean {
        return MediaUtil.isAvailableForVoiceInput(context)
    }

    override fun copyAssetstoSD() {
        AppResCopy.copyResFromAssetsToSD(context)
    }

    override fun permissionsToGet(): Array<String> {
        return arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun isArmDevice(): Boolean {
        return Build.CPU_ABI.contains("arm") && !Build.FINGERPRINT.contains("generic")
    }

    override fun setLanguage(language: String) {
        PrefManager.putString(Constant.LANGUAGE, language)
    }

    override fun initializeFFmpeg() {
        val ffmpeg = FFmpeg.getInstance(context)
        try {
            ffmpeg.loadBinary(object : LoadBinaryResponseHandler() {

                override fun onStart() {}

                override fun onFailure() {}

                override fun onSuccess() {}

                override fun onFinish() {}
            })
        } catch (e: FFmpegNotSupportedException) {
            // Handle if FFmpeg is not supported by device
        }
    }

    override fun convertAMRtoWAV(audioUri: Uri?, index: Int, listener: IUtilModel.onFFmpegCommandFinishedListener) {
        val ffmpeg = FFmpeg.getInstance(context)
        val pathToSave = Constants.DEFAULT_WORK_SPACE
        val filename = "recording$index.wav"
        val cmd: Array<String> = ("-i " + audioUri.toString() + " " + pathToSave + filename).split(" ").toTypedArray<String>()
        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(cmd, object : ExecuteBinaryResponseHandler() {

                override fun onStart() {}

                override fun onProgress(message: String?) {}

                override fun onFailure(message: String?) {}

                override fun onSuccess(message: String?) {
                    listener.onCommandFinished(index)
                }

                override fun onFinish() {}
            })
        } catch (e: FFmpegCommandAlreadyRunningException) {
            // Handle if FFmpeg is already running
        }
    }

    fun convertUriToByteArray(filename: String): ByteArray {
        val audioUri: Uri = Uri.parse(Constants.DEFAULT_WORK_SPACE + "/" + filename)

        val baos = ByteArrayOutputStream()
        val fileInputStream: FileInputStream
        try {
            fileInputStream = FileInputStream(File(audioUri.path))
            val buf: ByteArray = ByteArray(1024)
            var n: Int
            while (-1 != (fileInputStream.read(buf))) {
                n = fileInputStream.read(buf)
                baos.write(buf, 0, n)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val bbytes = baos.toByteArray()

        return bbytes
    }

    override fun getEncodedString(filename: String): String {
        return Base64.encodeToString(convertUriToByteArray(filename), Base64.DEFAULT)
    }

    override fun clearPrefs() {
        PrefManager.clearPrefs()
    }

}
