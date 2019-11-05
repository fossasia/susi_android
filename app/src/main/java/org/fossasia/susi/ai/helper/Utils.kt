package org.fossasia.susi.ai.helper

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import java.security.MessageDigest
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.rest.clients.BaseUrl
import org.fossasia.susi.ai.rest.responses.susi.Feedback
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import timber.log.Timber

object Utils {

    private val GRAVATAR_URL = "https://www.gravatar.com/avatar/"

    fun setSkillsImage(skillData: SkillData, imageView: ImageView) {
        Picasso.get()
                .load(getImageLink(skillData))
                .error(R.drawable.ic_susi)
                .transform(CircleTransform())
                .fit()
                .centerCrop()
                .into(imageView)
    }

    fun getImageLink(skillData: SkillData): String {
        val link = "${BaseUrl.SUSI_DEFAULT_BASE_URL}/cms/getImage.png?model=${skillData.model}&language=${skillData.language}&group=${skillData.group}&image=${skillData.image}"
                .replace(" ", "%20")
        Timber.d("SUSI URI $link")
        return link
    }

    fun setAvatar(context: Context, avatarUrl: String?, imageView: ImageView) {
        Picasso.get()
                .load(avatarUrl)
                .fit().centerCrop()
                .error(R.drawable.ic_susi)
                .transform(CircleTransform())
                .into(imageView)
    }

    fun toMd5Hash(email: String?): String? {
        try {
            val md5 = MessageDigest.getInstance("MD5")
            val md5HashBytes: Array<Byte> = md5.digest(email?.toByteArray()).toTypedArray()
            return byteArrayToString(md5HashBytes)
        } catch (e: Exception) {
            return null
        }
    }

    fun byteArrayToString(array: Array<Byte>): String {
        val result = StringBuilder(array.size * 2)
        for (byte: Byte in array) {
            val toAppend: String = String.format("%x", byte).replace(" ", "0")
            result.append(toAppend)
        }
        return result.toString()
    }

    fun truncateEmailAtEnd(email: String?): String? {
        if (!email.isNullOrEmpty()) {
            val truncateAt = email?.indexOf('@')
            if (truncateAt != null && truncateAt != -1) {
                return email.substring(0, truncateAt.plus(1)) + " ..."
            }
        }
        return null
    }

    fun setUsername(feedback: Feedback, feedbackEmail: TextView) {
        if (!feedback.userName.isNullOrEmpty()) {
            feedbackEmail.text = feedback.userName
        } else {
            if (PrefManager.token != null) {
                if (!feedback.email.equals(PrefManager.getString(Constant.EMAIL, null), true)) {
                    Utils.truncateEmailAtEnd(feedback.email)?.let { feedbackEmail?.text = it }
                } else {
                    feedbackEmail.text = feedback.email
                }
            } else {
                Utils.truncateEmailAtEnd(feedback.email)?.let { feedbackEmail?.text = it }
            }
        }
    }

    fun hideSoftKeyboard(context: Context?, view: View) {
        val inputManager: InputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.SHOW_FORCED)
    }
}
