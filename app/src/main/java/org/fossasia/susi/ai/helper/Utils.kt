package org.fossasia.susi.ai.helper

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Picasso
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.rest.clients.BaseUrl
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import java.net.URI
import timber.log.Timber
import java.security.MessageDigest

object Utils {

    private val GRAVATAR_URL = "https://www.gravatar.com/avatar/"

    fun setSkillsImage(skillData: SkillData, imageView: ImageView) {
        Picasso.with(imageView.context)
                .load(getImageLink(skillData))
                .error(R.drawable.ic_susi)
                .transform(CircleTransform())
                .fit()
                .centerCrop()
                .into(imageView)
    }

    fun setAvatar(context: Context, email: String?, imageView: ImageView) {
        val imageUrl: String = GRAVATAR_URL + toMd5Hash(email) + ".jpg"
        Picasso.with(context)
                .load(imageUrl)
                .fit().centerCrop()
                .error(R.drawable.ic_susi)
                .transform(CircleTransform())
                .into(imageView)
    }

    fun getImageLink(skillData: SkillData): String {
        val uri = URI("${BaseUrl.SUSI_DEFAULT_BASE_URL}/cms/getImage.png?model=${skillData.model}&language=${skillData.language}&group=${skillData.group}&image=${skillData.image}")
        return uri.toASCIIString()
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

}