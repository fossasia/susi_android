package org.fossasia.susi.ai.skills.skilldetails

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_skill_details.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.ChatActivity
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.rest.responses.susi.Ratings
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.rest.responses.susi.Stars
import org.fossasia.susi.ai.skills.SkillsActivity
import org.fossasia.susi.ai.skills.skilldetails.adapters.recycleradapters.SkillExamplesAdapter
import org.fossasia.susi.ai.skills.skilldetails.contract.ISkillDetailsPresenter
import org.fossasia.susi.ai.skills.skilldetails.contract.ISkillDetailsView
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

/**
 *
 * Created by chiragw15 on 24/8/17.
 */
class SkillDetailsFragment : Fragment(), ISkillDetailsView {

    private lateinit var skillDetailsPresenter: ISkillDetailsPresenter

    private lateinit var skillData: SkillData
    private lateinit var skillGroup: String
    private lateinit var skillTag: String
    private val imageLink = "https://raw.githubusercontent.com/fossasia/susi_skill_data/master/models/general/"

    private lateinit var fiveStarSkillRatingBar: RatingBar
    private lateinit var fiveStarSkillRatingScaleTextView: TextView
    private lateinit var fiveStarAverageSkillRating: TextView
    private lateinit var fiveStarTotalSkillRating: TextView
    private lateinit var skillRatingChart: HorizontalBarChart
    private lateinit var xAxis: XAxis

    companion object {
        const val SKILL_KEY = "skill_key"
        const val SKILL_TAG = "skill_tag"
        const val SKILL_GROUP = "skill_group"
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
        skillDetailsPresenter = SkillDetailsPresenter(this)
        skillDetailsPresenter.onAttach(this)
        skillData = arguments?.getSerializable(
                SKILL_KEY) as SkillData
        skillGroup = (arguments as Bundle).getString(SKILL_GROUP)
        skillTag = (arguments as Bundle).getString(SKILL_TAG)
        return inflater.inflate(R.layout.fragment_skill_details, container, false)
    }

    @NonNull
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (skillData.skillName != null && !skillData.skillName.isEmpty())
            (activity as SkillsActivity).title = skillData.skillName
        setupUI()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupUI() {
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

    private fun setImage() {
        skillDetailImage.setImageResource(R.drawable.ic_susi)
        if (skillData.image != null && !skillData.image.isEmpty()) {
            Picasso.with(activity?.applicationContext).load(StringBuilder(imageLink)
                    .append(skillGroup).append("/en/").append(skillData.image).toString())
                    .error(R.drawable.ic_susi)
                    .fit().centerCrop()
                    .into(skillDetailImage)
        }
    }

    private fun setName() {
        skillDetailTitle.text = activity?.getString(R.string.no_skill_name)
        if (skillData.skillName != null && !skillData.skillName.isEmpty()) {
            skillDetailTitle.text = skillData.skillName
        }
    }

    private fun setAuthor() {
        skillDetailAuthor.text = "Author : ${activity?.getString(R.string.no_skill_author)}"
        if (skillData.author != null && !skillData.author.isEmpty()) {
            if (skillData.authorUrl == null || skillData.authorUrl.isEmpty())
                skillDetailAuthor.text = "Author : ${skillData.skillName}"
            else {
                skillDetailAuthor.setOnClickListener({
                    try {
                        var uri = Uri.parse(skillData.authorUrl)
                        var builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder() //custom tabs intent builder
                        var customTabsIntent = builder.build()
                        customTabsIntent.launchUrl(context, uri) //launching through custom tabs
                    } catch (e: Exception) {
                        Toast.makeText(context, getString(R.string.link_unavailable), Toast.LENGTH_SHORT).show()
                    }
                })
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    skillDetailAuthor.text = Html.fromHtml("Author : <a href=\"${skillData.authorUrl}\">${skillData.author}</a>", Html.FROM_HTML_MODE_COMPACT)
                } else {
                    skillDetailAuthor.text = Html.fromHtml("Author : <a href=\"${skillData.authorUrl}\">${skillData.author}</a>")
                }
            }
        }
    }

    private fun setTryButton() {
        if (skillData.examples == null || skillData.examples.isEmpty())
            skillDetailTryButton.visibility = View.GONE

        skillDetailTryButton.setOnClickListener {
            activity?.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
            val intent = Intent(activity, ChatActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            if (skillData.examples != null && skillData.examples.isNotEmpty())
                intent.putExtra("example", skillData.examples[0])
            else
                intent.putExtra("example", "")
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun setShareButton() {
        if (skillTag == null || skillTag.isEmpty()) {
            skillDetailShareButton.visibility = View.GONE
            return
        }

        skillDetailShareButton.setOnClickListener {
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
            context?.startActivity(Intent.createChooser(sendIntent, getString(R.string.share_skill)))
        }
    }

    private fun setDescription() {
        skillDetailDescription.text = activity?.getString(R.string.no_skill_description)
        if (skillData.descriptions != null && !skillData.descriptions.isEmpty()) {
            skillDetailDescription.text = skillData.descriptions
        }
    }

    private fun setExamples() {
        if (skillData.examples != null) {
            skillDetailExamples.setHasFixedSize(true)
            val mLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            skillDetailExamples.layoutManager = mLayoutManager
            skillDetailExamples.adapter = SkillExamplesAdapter(requireContext(), skillData.examples)
        }
    }

    /**
     * Set up the rating section.
     *
     * Display 5 star rating bar.
     *
     * If the number of ratings is positive, display horizontal bar chart to display
     * the percentage of users for each rating on a scale of one to five stars otherwise
     * display a message to inform the user that the skill is unrated.
     */
    private fun setRating() {

        //If the user is logged in, set up the five star skill rating bar
        if (PrefManager.getToken() != null) {
            val map: MutableMap<String, String> = HashMap()
            map.put("model", skillData.model)
            map.put("group", skillData.group)
            map.put("language", skillData.language)
            map.put("skill", skillTag)
            map.put("access_token", PrefManager.getToken().toString())
            skillDetailsPresenter.updateUserRating(map)
            setUpFiveStarRatingBar()
        }

        //If the totalStar is positive, it implies that the skill has been rated
        //If so, set up the section to display the statistics else simply display a message for unrated skill
        if (skillData.skillRating?.stars?.totalStar!! > 0) {
            fiveStarAverageSkillRating = tv_average_rating
            fiveStarTotalSkillRating = tv_total_rating

            fiveStarTotalSkillRating.text = skillData.skillRating?.stars?.totalStar.toString()
            fiveStarAverageSkillRating.text = skillData.skillRating?.stars?.averageStar.toString()
            setSkillGraph()
        } else {
            skill_rating_view.visibility = View.GONE
            tv_unrated_skill.visibility = View.VISIBLE
            if (PrefManager.getToken() != null) {
                tv_unrated_skill.text = getString(R.string.skill_unrated)
            } else {
                tv_unrated_skill.text = getString(R.string.skill_unrated_for_anonymous_user)
            }
        }
    }

    /**
     * Set up the five star rating bar using which the user can rate the skill
     * on a scale of one to five stars.
     *
     * Change the contents of the rating scale text view as per the number of
     * stars given by the user.
     */
    private fun setUpFiveStarRatingBar() {
        fiveStarSkillRatingBar = five_star_skill_rating_bar
        fiveStarSkillRatingScaleTextView = tv_five_star_skill_rating_scale

        tvFiveStarSkillRatingBar.visibility = View.VISIBLE
        fiveStarSkillRatingBar.visibility = View.VISIBLE

        //Set up the OnRatingCarChange listener to change the rating scale text view contents accordingly
        fiveStarSkillRatingBar.setOnRatingBarChangeListener({ ratingBar, v, b ->

            fiveStarSkillRatingScaleTextView.visibility = View.VISIBLE

            val map: MutableMap<String, String> = HashMap()
            map.put("model", skillData.model)
            map.put("group", skillData.group)
            map.put("language", skillData.language)
            map.put("skill", skillTag)
            map.put("stars", v.toInt().toString())
            map.put("access_token", PrefManager.getToken().toString())
            skillDetailsPresenter.updateRatings(map)

            fiveStarSkillRatingScaleTextView.setText(v.toString())
            when (ratingBar.rating.toInt()) {
                1 -> fiveStarSkillRatingScaleTextView.setText(R.string.rate_hate)
                2 -> fiveStarSkillRatingScaleTextView.setText(R.string.rate_improvement)
                3 -> fiveStarSkillRatingScaleTextView.setText(R.string.rate_good)
                4 -> fiveStarSkillRatingScaleTextView.setText(R.string.rate_great)
                5 -> fiveStarSkillRatingScaleTextView.setText(R.string.rate_awesome)
                else -> fiveStarSkillRatingScaleTextView.setText("")
            }

            //Display a toast to notify the user that the rating has been submitted
            Toast.makeText(context, getString(R.string.toast_thank_for_rating), Toast.LENGTH_SHORT).show()
            setRating()
        })
    }

    /**
     * Update the ratings as soon as the user rates a skill
     *
     * @param ratingsObject Updated stars object that includes the user rating
     *
     */
    override fun updateRatings(ratingsObject: Stars?) {
        if (ratingsObject != null) {
            skillData.skillRating?.stars = ratingsObject
            setRating()
        }
    }

    /**
     * Show the user rating on the rating bar
     *
     * @param updatedRating Updates the rating bar with the user rating
     *
     */
    override fun updateUserRating(updatedRating: Int?) {
        if (updatedRating != null) {
            fiveStarSkillRatingBar.rating = updatedRating.toFloat()
        }
    }

    /**
     * Set up the axes along with other necessary details for the horizontal bar chart.
     */
    private fun setSkillGraph() {
        skillRatingChart = skill_rating_chart
        skillRatingChart.setPinchZoom(false)
        skillRatingChart.isDoubleTapToZoomEnabled = false
        skillRatingChart.setScaleEnabled(false)

        skillRatingChart.setDrawBarShadow(false)
        val description = Description()
        description.text = ""
        skillRatingChart.description = description
        skillRatingChart.legend.setEnabled(false)
        skillRatingChart.setPinchZoom(false)
        skillRatingChart.setDrawValueAboveBar(false)

        //Display the axis on the left (contains the labels 1*, 2* and so on)
        xAxis = skillRatingChart.getXAxis()
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setPosition(XAxis.XAxisPosition.TOP)
        xAxis.setEnabled(true)

        val yLeft = skillRatingChart.axisLeft
        yLeft.axisMaximum = 100f
        yLeft.axisMinimum = 0f
        yLeft.isEnabled = false

        val yRight = skillRatingChart.axisRight
        yRight.setDrawAxisLine(true)
        yRight.setDrawGridLines(false)
        yRight.isEnabled = false

        //Set bar entries and add necessary formatting
        setData()

        //Add animation to the graph
        skillRatingChart.animateY(2000)
    }

    /**
     * Set the bar entries i.e. the percentage of users who rated the skill with
     * a certain number of stars.
     *
     * Set the colors for different bars and the bar width of the bars.
     */
    private fun setData() {

        val totalUsers: Int = skillData.skillRating?.stars?.totalStar!!
        val oneStarUsers: Int = skillData.skillRating?.stars?.oneStar!!
        val twoStarUsers: Int = skillData.skillRating?.stars?.twoStar!!
        val threeStarUsers: Int = skillData.skillRating?.stars?.threeStar!!
        val fourStarUsers: Int = skillData.skillRating?.stars?.fourStar!!
        val fiveStarUsers: Int = skillData.skillRating?.stars?.fiveStar!!

        val oneStarUsersPercent: Float = calcPercentageOfUsers(oneStarUsers, totalUsers)
        val twoStarUsersPercent: Float = calcPercentageOfUsers(twoStarUsers, totalUsers)
        val threeStarUsersPercent: Float = calcPercentageOfUsers(threeStarUsers, totalUsers)
        val fourStarUsersPercent: Float = calcPercentageOfUsers(fourStarUsers, totalUsers)
        val fiveStarUsersPercent: Float = calcPercentageOfUsers(fiveStarUsers, totalUsers)

        val values = arrayOf(oneStarUsers.toString() + " (" + oneStarUsersPercent.toInt().toString() + "%)",
                twoStarUsers.toString() + " (" + twoStarUsersPercent.toInt().toString() + "%)",
                threeStarUsers.toString() + " (" + threeStarUsersPercent.toInt().toString() + "%)",
                fourStarUsers.toString() + " (" + fourStarUsersPercent.toInt().toString() + "%)",
                fiveStarUsers.toString() + " (" + fiveStarUsersPercent.toInt().toString() + "%)")

        //Set label count to 5 as we are using 5 star rating system
        xAxis.setLabelCount(5)
        xAxis.valueFormatter = XAxisValueFormatter(values)

        //Add a list of bar entries
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, calcPercentageOfUsers(oneStarUsers, totalUsers)))
        entries.add(BarEntry(1f, calcPercentageOfUsers(twoStarUsers, totalUsers)))
        entries.add(BarEntry(2f, calcPercentageOfUsers(threeStarUsers, totalUsers)))
        entries.add(BarEntry(3f, calcPercentageOfUsers(fourStarUsers, totalUsers)))
        entries.add(BarEntry(4f, calcPercentageOfUsers(fiveStarUsers, totalUsers)))

        val barDataSet = BarDataSet(entries, "Bar Data Set")
        barDataSet.barShadowColor = Color.argb(40, 150, 150, 150)
        barDataSet.setDrawValues(false)

        //Set the colors for bars with first color for 1*, second for 2* and so on
        barDataSet.setColors(
                ContextCompat.getColor(skillRatingChart.context, R.color.md_red_300),
                ContextCompat.getColor(skillRatingChart.context, R.color.md_orange_300),
                ContextCompat.getColor(skillRatingChart.context, R.color.md_yellow_300),
                ContextCompat.getColor(skillRatingChart.context, R.color.md_light_green_300),
                ContextCompat.getColor(skillRatingChart.context, R.color.md_green_300)
        )

        val data = BarData(barDataSet)

        //Set the bar width
        //Note : To increase the spacing between the bars set the value of barWidth to < 1f
        data.barWidth = 0.9f
        skillRatingChart.setDrawBarShadow(true)

        //Finally set the data and refresh the graph
        skillRatingChart.data = data
        skillRatingChart.setViewPortOffsets(0f, 28f, 128f, 28f)
        skillRatingChart.invalidate()
    }

    /**
     * Returns the percentage of users corresponding to each rating
     *
     * @param actualNumberOfUsers : Actual number of users corresponding to a rating
     * @param totalNumberOfUsers : Total number of ratings for a skill
     */
    private fun calcPercentageOfUsers(actualNumberOfUsers: Int, totalNumberOfUsers: Int): Float {
        return (actualNumberOfUsers * 100f) / totalNumberOfUsers
    }

    private fun setDynamicContent() {
        if (skillData.dynamicContent == null) {
            skillDetailContent.visibility = View.GONE
        } else {
            if (skillData.dynamicContent!!) {
                skillDetailContent.text = context?.getString(R.string.content_type_dynamic)
            } else {
                skillDetailContent.text = context?.getString(R.string.content_type_static)
            }
        }
    }

    private fun setPolicy() {
        if (skillData.developerPrivacyPolicy == null || skillData.developerPrivacyPolicy.isEmpty()) {
            skillDetailPolicy.visibility = View.GONE
        } else {
            skillDetailAuthor.setOnClickListener({
                try {
                    var uri = Uri.parse(skillData.developerPrivacyPolicy)
                    var builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder() //custom tabs intent builder
                    var customTabsIntent = builder.build()
                    customTabsIntent.launchUrl(context, uri) //launching through custom tabs
                } catch (e: Exception) {
                    Toast.makeText(context, getString(R.string.link_unavailable), Toast.LENGTH_SHORT).show()
                }
            })
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                skillDetailPolicy.text = Html.fromHtml("<a href=\"${skillData.developerPrivacyPolicy}\">Developer's Privacy Policy</a>", Html.FROM_HTML_MODE_COMPACT)
            } else {
                skillDetailPolicy.text = Html.fromHtml("<a href=\"${skillData.developerPrivacyPolicy}\">Developer's Privacy Policy</a>")
            }
        }
    }

    private fun setTerms() {
        if (skillData.termsOfUse == null || skillData.termsOfUse.isEmpty()) {
            skillDetailTerms.visibility = View.GONE
        } else {
            skillDetailAuthor.setOnClickListener({
                try {
                    var uri = Uri.parse(skillData.termsOfUse)
                    var builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder() //custom tabs intent builder
                    var customTabsIntent = builder.build()
                    customTabsIntent.launchUrl(context, uri) //launching through custom tabs
                } catch (e: Exception) {
                    Toast.makeText(context, getString(R.string.link_unavailable), Toast.LENGTH_SHORT).show()
                }
            })
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                skillDetailTerms.text = Html.fromHtml("<a href=\"${skillData.termsOfUse}\">Terms of use</a>", Html.FROM_HTML_MODE_COMPACT)
            } else {
                skillDetailTerms.text = Html.fromHtml("<a href=\"${skillData.termsOfUse}\">Terms of use</a>")
            }
        }
    }
}
