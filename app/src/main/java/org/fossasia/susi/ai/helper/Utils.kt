package org.fossasia.susi.ai.helper

import android.widget.ImageView
import com.squareup.picasso.Picasso
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.rest.clients.BaseUrl
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import timber.log.Timber

object Utils {

    fun setSkillsImage(skillData: SkillData, imageView: ImageView) {
        Picasso.with(imageView.context)
                .load(getImageLink(skillData))
                .error(R.drawable.ic_susi)
                .fit()
                .centerCrop()
                .into(imageView)
    }

    fun getImageLink(skillData: SkillData): String {
        val link = "${BaseUrl.SUSI_DEFAULT_BASE_URL}/cms/getImage.png?model=${skillData.model}&language=${skillData.language}&group=${skillData.group}&image=${skillData.image}"
                .replace(" ", "%20")
        Timber.d("SUSI URI" + link)
        return link
    }
}
