package org.fossasia.susi.ai.helper

import android.widget.ImageView
import com.squareup.picasso.Picasso
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.rest.clients.BaseUrl
import org.fossasia.susi.ai.rest.responses.susi.SkillData

object Utils {
    private val imageLink = BaseUrl.SUSI_DEFAULT_BASE_URL + "/cms/getImage.png?"

    fun setSkillsImage(skillData: SkillData, imageView: ImageView) {
        Picasso.with(imageView.context)
                .load(getImageLink(skillData))
                .error(R.drawable.ic_susi)
                .fit()
                .centerCrop()
                .into(imageView)
    }

    fun getImageLink(skillData: SkillData): String {
        return "$imageLink" + "model=" + "$skillData.model" + "&language=" + "$skillData.language" + "&group=" +
                StringBuffer(skillData.group.replace(" ", "%20")).toString() + "&image=" + "$skillData.image"
    }

}
