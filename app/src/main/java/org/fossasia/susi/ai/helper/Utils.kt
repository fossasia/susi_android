package org.fossasia.susi.ai.helper

import android.widget.ImageView
import com.squareup.picasso.Picasso
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.rest.clients.BaseUrl
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import timber.log.Timber

object Utils {

    lateinit var skill: SkillData
    fun setSkillsImage(skillData: SkillData, imageView: ImageView) {
        skill = skillData
        Picasso.with(imageView.context)
                .load(getImageLink())
                .error(R.drawable.ic_susi)
                .fit()
                .centerCrop()
                .into(imageView)
    }

    fun getImageLink(): String {
        val link = "${BaseUrl.SUSI_DEFAULT_BASE_URL}/cms/getImage.png?model=${skill.model}&language=${skill.language}&group=${skill.group}&image=${skill.image}"
                .replace(" ","%20")
        Timber.d("SUSI URI" + link)
        ValidateUtils.validateUrl(link)
        return link
    }

}
