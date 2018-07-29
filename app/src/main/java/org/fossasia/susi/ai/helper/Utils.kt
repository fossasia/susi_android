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
                .load(getLink(skillData))
                .error(R.drawable.ic_susi)
                .fit()
                .centerCrop()
                .into(imageView)
    }

    fun getLink(skillData: SkillData): String {
        return StringBuilder(imageLink)
                .append("model=" + skillData.model + "&language=" + skillData.language + "&group=" + skillData.group.replace(" ", "%20"))
                .append("&image=").append(skillData.image).toString()
    }

}
