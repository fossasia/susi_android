package org.fossasia.susi.ai.skills.skilldetails

import android.support.v4.app.Fragment
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_skill_details.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.ChatActivity
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.skills.SkillsActivity
import org.fossasia.susi.ai.skills.skilldetails.adapters.recycleradapters.SkillExamplesAdapter
import java.io.Serializable
import android.net.Uri

/**
 *
 * Created by chiragw15 on 24/8/17.
 */
class SkillDetailsFragment: Fragment() {

    lateinit var skillData: SkillData
    lateinit var skillGroup: String
    lateinit var skillTag: String
    val imageLink = "https://raw.githubusercontent.com/fossasia/susi_skill_data/master/models/general/"

    companion object {
        val SKILL_KEY = "skill_key"
        val SKILL_TAG = "skill_tag"
        val SKILL_GROUP = "skill_group"
        fun newInstance(skillData: SkillData, skillGroup: String, skillTag: String): SkillDetailsFragment {
            val fragment = SkillDetailsFragment()
            val bundle = Bundle()
            bundle.putSerializable(SKILL_KEY, skillData as Serializable)
            bundle.putString(SKILL_GROUP, skillGroup)
            bundle.putString(SKILL_TAG, skillTag)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        skillData = arguments.getSerializable(
                SKILL_KEY) as SkillData
        skillGroup = arguments.getString(SKILL_GROUP)
        skillTag = arguments.getString(SKILL_TAG)
        return inflater.inflate(R.layout.fragment_skill_details, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        if(skillData.skillName != null && !skillData.skillName.isEmpty())
            (activity as SkillsActivity).title = skillData.skillName
        setupUI()
        super.onViewCreated(view, savedInstanceState)
    }

    fun setupUI() {
        setImage()
        setName()
        setAuthor()
        setTryButton()
        setShareButton()
        setDescription()
        setExamples()
        setRating()
        setDynamicContent()
        setPolicy()
        setTerms()
    }

    fun setImage() {
        skill_detail_image.setImageResource(R.drawable.ic_susi)
        if(skillData.image != null && !skillData.image.isEmpty()){
            Picasso.with(activity.applicationContext).load(StringBuilder(imageLink)
                    .append(skillGroup).append("/en/").append(skillData.image).toString())
                    .error(R.drawable.ic_susi)
                    .fit().centerCrop()
                    .into(skill_detail_image)
        }
    }

    fun setName() {
        skill_detail_title.text = activity.getString(R.string.no_skill_name)
        if(skillData.skillName != null && !skillData.skillName.isEmpty()){
            skill_detail_title.text = skillData.skillName
        }
    }

    fun setAuthor() {
        skill_detail_author.text = "Author : ${activity.getString(R.string.no_skill_author)}"
        if(skillData.author != null && !skillData.author.isEmpty()){
            if(skillData.authorUrl == null || skillData.authorUrl.isEmpty())
                skill_detail_author.text = "Author : ${skillData.skillName}"
            else {
                skill_detail_author.linksClickable = true
                skill_detail_author.movementMethod = LinkMovementMethod.getInstance()
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    skill_detail_author.text = Html.fromHtml("Author : <a href=\"${skillData.authorUrl}\">${skillData.author}</a>", Html.FROM_HTML_MODE_COMPACT)
                } else {
                    skill_detail_author.text = Html.fromHtml("Author : <a href=\"${skillData.authorUrl}\">${skillData.author}</a>")
                }
            }
        }
    }

    fun setTryButton() {
        if(skillData.examples == null || skillData.examples.isEmpty())
            skill_detail_try_button.visibility = View.GONE

        skill_detail_try_button.setOnClickListener {
            activity.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
            val intent = Intent(activity, ChatActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            if(skillData.examples != null && skillData.examples.isNotEmpty())
                intent.putExtra("example",skillData.examples[0])
            else
                intent.putExtra("example","")
            startActivity(intent)
            activity.finish()
        }
    }

    fun setShareButton() {
        if(skillTag == null || skillTag.isEmpty()) {
            skill_detail_share_button.visibility = View.GONE
            return
        }

        skill_detail_share_button.setOnClickListener {
            val shareUriBuilder = Uri.Builder()
            shareUriBuilder.scheme("https")
                    .authority("skills.susi.ai")
                    .appendPath(skillGroup)
                    .appendPath(skillTag)
                    .appendPath("en")
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareUriBuilder.build().toString())
            sendIntent.type = "text/plain"
            context.startActivity(sendIntent)
        }
    }

    fun setDescription() {
        skill_detail_description.text = activity.getString(R.string.no_skill_description)
        if( skillData.descriptions != null && !skillData.descriptions.isEmpty()){
            skill_detail_description.text = skillData.descriptions
        }
    }

    fun setExamples() {
        if(skillData.examples != null) {
            skill_detail_examples.setHasFixedSize(true)
            val mLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            skill_detail_examples.layoutManager = mLayoutManager
            skill_detail_examples.adapter = SkillExamplesAdapter(activity, skillData.examples)
        }
    }

    fun setRating() {
        if(skillData.skillRating == null) {
            skill_detail_rating_positive.text = "Skill not rated yet"
            val params = skill_detail_rating_positive.layoutParams
            params.width = 344
            skill_detail_rating_positive.layoutParams = params
            skill_detail_rating_negative.visibility = View.GONE
            skill_detail_rating_thumbs_up.visibility = View.GONE
            skill_detail_rating_thumbs_down.visibility = View.GONE
        } else {
            skill_detail_rating_positive.text = "Positive: " + skillData.skillRating?.positive
            skill_detail_rating_negative.text = "Negative: " + skillData.skillRating?.negative

            val positiveColor = getRatedColor(skillData.skillRating?.positive, "Positive")
            val negativeColor = getRatedColor(skillData.skillRating?.negative, "Negative")

            skill_detail_rating_thumbs_up.setColorFilter(positiveColor)
            skill_detail_rating_thumbs_down.setColorFilter(negativeColor)
        }
    }

    private fun getRatedColor(rating: Int?, s: String?): Int {
        var resId = R.color.colorAccent
        if (rating != null) {
            when (s) {
                "Negative" ->
                        if (rating < 10)
                            resId = R.color.md_red_100
                        else if (rating in 10..19)
                            resId = R.color.md_red_200
                        else if (rating in 20..29)
                            resId = R.color.md_red_300
                        else if (rating in 30..39)
                            resId = R.color.md_red_300
                        else if (rating in 40..49)
                            resId = R.color.md_red_300
                        else
                            resId = R.color.md_red_400
                "Positive" ->
                    if (rating < 10)
                        resId = R.color.md_blue_100
                    else if (rating in 10..19)
                        resId = R.color.md_blue_200
                    else if (rating in 20..29)
                        resId = R.color.md_blue_300
                    else if (rating in 30..39)
                        resId = R.color.md_blue_300
                    else if (rating in 40..49)
                        resId = R.color.md_blue_300
                    else
                        resId = R.color.md_blue_400
            }
        }
        return resources.getColor(resId)
    }

    fun setDynamicContent() {
        if(skillData.dynamicContent == null) {
            skill_detail_content.visibility = View.GONE
        } else {
            if(skillData.dynamicContent!!) {
                skill_detail_content.text = "Content type: Dynamic"
            } else {
                skill_detail_content.text = "Content type: Static"
            }
        }
    }

    fun setPolicy() {
        if(skillData.developerPrivacyPolicy == null || skillData.developerPrivacyPolicy.isEmpty()) {
            skill_detail_policy.visibility = View.GONE
        } else {
            skill_detail_policy.linksClickable = true
            skill_detail_policy.movementMethod = LinkMovementMethod.getInstance()
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                skill_detail_policy.text = Html.fromHtml("<a href=\"${skillData.developerPrivacyPolicy}\">Developer's Privacy Policy</a>", Html.FROM_HTML_MODE_COMPACT)
            } else {
                skill_detail_policy.text = Html.fromHtml("<a href=\"${skillData.developerPrivacyPolicy}\">Developer's Privacy Policy</a>")
            }
        }
    }

    fun setTerms() {
        if(skillData.termsOfUse == null || skillData.termsOfUse.isEmpty()) {
            skill_detail_terms.visibility = View.GONE
        } else {
            skill_detail_terms.linksClickable = true
            skill_detail_terms.movementMethod = LinkMovementMethod.getInstance()
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                skill_detail_terms.text = Html.fromHtml("<a href=\"${skillData.termsOfUse}\">Terms of use</a>", Html.FROM_HTML_MODE_COMPACT)
            } else {
                skill_detail_terms.text = Html.fromHtml("<a href=\"${skillData.termsOfUse}\">Terms of use</a>")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
