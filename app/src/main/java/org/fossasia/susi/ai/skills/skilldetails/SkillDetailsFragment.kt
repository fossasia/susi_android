package org.fossasia.susi.ai.skills.skilldetails

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.main.fragment_skill_details.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.ChatActivity
import org.fossasia.susi.ai.dataclasses.FetchFeedbackQuery
import org.fossasia.susi.ai.dataclasses.PostFeedback
import org.fossasia.susi.ai.dataclasses.ReportSkillQuery
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.helper.Utils
import org.fossasia.susi.ai.login.LoginActivity
import org.fossasia.susi.ai.login.LoginLogoutModulePresenter
import org.fossasia.susi.ai.login.contract.ILoginLogoutModulePresenter
import org.fossasia.susi.ai.rest.responses.susi.GetSkillFeedbackResponse
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.rest.responses.susi.Stars
import org.fossasia.susi.ai.skills.skilldetails.adapters.recycleradapters.FeedbackAdapter
import org.fossasia.susi.ai.skills.skilldetails.adapters.recycleradapters.SkillExamplesAdapter
import org.fossasia.susi.ai.skills.skilldetails.contract.ISkillDetailsPresenter
import org.fossasia.susi.ai.skills.skilldetails.contract.ISkillDetailsView
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class SkillDetailsFragment : Fragment(), ISkillDetailsView {

    private val skillDetailsPresenter: ISkillDetailsPresenter by inject { parametersOf(this) }
    private lateinit var loginLogoutModulePresenter: ILoginLogoutModulePresenter

    private lateinit var skillData: SkillData
    private lateinit var skillGroup: String
    private lateinit var skillTag: String

    private var fromUser = false
    private lateinit var skillRatingChart: HorizontalBarChart
    private lateinit var xAxis: XAxis

    companion object {
        const val SKILL_KEY = "skill_key"
        const val SKILL_TAG = "skill_tag"
        const val SKILL_GROUP = "skill_group"
        fun newInstance(skillData: SkillData?, skillGroup: String?, skillTag: String): SkillDetailsFragment {
            val fragment = SkillDetailsFragment()
            val bundle = Bundle()
            bundle.putParcelable(SKILL_KEY, skillData)
            bundle.putString(SKILL_GROUP, skillGroup)
            bundle.putString(SKILL_TAG, skillTag)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        loginLogoutModulePresenter = LoginLogoutModulePresenter(requireContext())
        skillData = arguments?.getParcelable(
                SKILL_KEY) as SkillData
        skillGroup = arguments?.getString(SKILL_GROUP).toString()
        skillTag = arguments?.getString(SKILL_TAG).toString()
        return inflater.inflate(R.layout.fragment_skill_details, container, false)
    }

    @NonNull
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        skillData.skillName.let { activity?.title = skillData.skillName }
        setupUI()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupUI() {
        setImage()
        setName()
        setAuthor()
        setReportButton()
        setTryButton()
        setShareButton()
        setDescription()
        setExamples()
        setRating()
        setFeedback()
        setDynamicContent()
    }

    private fun setImage() {
        skillDetailImage.setImageResource(R.drawable.ic_susi)
        if (!TextUtils.isEmpty(skillData.image)) {
            Utils.setSkillsImage(skillData, skillDetailImage)
        }
    }

    private fun setName() {
        skillDetailTitle.text = activity?.getString(R.string.no_skill_name)
        if (!TextUtils.isEmpty(skillData.skillName)) {
            skillDetailTitle.text = skillData.skillName
        }
    }

    @SuppressLint("InflateParams")
    private fun setReportButton() {

        if (PrefManager.token != null) {
            reportSkill.visibility = View.VISIBLE
        }

        reportSkill.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            val view = layoutInflater.inflate(R.layout.alert_report_skill, null)
            val reportedUserMessage = view.findViewById(R.id.report_message) as EditText
            dialogBuilder.setView(view)
            dialogBuilder.setTitle(R.string.report_skill)

            dialogBuilder.setPositiveButton(R.string.report_send) { _, _ ->
                if (PrefManager.token != null && reportedUserMessage.text.isNotEmpty()) {
                    val queryObject = ReportSkillQuery(skillData.model, skillData.group, skillTag,
                            reportedUserMessage.text.toString(), PrefManager.token.toString())

                    skillDetailsPresenter.sendReport(queryObject)
                } else {
                    updateSkillReportStatus(getString(R.string.error))
                }
            }

            dialogBuilder.setNegativeButton(R.string.cancel) { dialog, whichButton ->
                dialog.dismiss()
            }

            dialogBuilder.show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setAuthor() {
        skillDetailAuthor.text = "by ${activity?.getString(R.string.no_skill_author)}"
        if (!TextUtils.isEmpty(skillData.author)) {
            if (TextUtils.isEmpty(skillData.authorUrl))
                skillDetailAuthor.text = "by ${skillData.skillName}"
            else {
                skillDetailAuthor.setOnClickListener {
                    try {
                        val uri = Uri.parse(skillData.authorUrl)
                        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder() // custom tabs intent builder
                        val customTabsIntent = builder.build()
                        customTabsIntent.launchUrl(context, uri) // launching through custom tabs
                    } catch (e: Exception) {
                        Toast.makeText(context, getString(R.string.link_unavailable), Toast.LENGTH_SHORT).show()
                    }
                }
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    skillDetailAuthor.text = Html.fromHtml("by <a href=\"${skillData.authorUrl}\">${skillData.author}</a>", Html.FROM_HTML_MODE_COMPACT)
                } else {
                    skillDetailAuthor.text = Html.fromHtml("by <a href=\"${skillData.authorUrl}\">${skillData.author}</a>")
                }
            }
        }
    }

    private fun setTryButton() {
        if (skillData.examples.isEmpty() || skillData.examples.isEmpty())
            skillDetailTryButton.visibility = View.GONE

        skillDetailTryButton.setOnClickListener {
            activity?.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
            val intent = Intent(activity, ChatActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            if (skillData.examples.isNotEmpty() && skillData.examples.isNotEmpty())
                intent.putExtra("example", skillData.examples[0])
            else
                intent.putExtra("example", "")
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun setShareButton() {
        if (TextUtils.isEmpty(skillTag)) {
            skillDetailShareButton.visibility = View.GONE
            return
        }

        skillDetailShareButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_share_white_24dp, 0, 0, 0)
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
        if (!TextUtils.isEmpty(skillData.descriptions)) {
            skillDetailDescription.text = skillData.descriptions
        }
    }

    private fun setExamples() {
        if (skillData.examples.isNotEmpty() && skillData.examples.isNotEmpty()) {
            skillDetailExamples.setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            skillDetailExamples.layoutManager = layoutManager
            skillDetailExamples.adapter = SkillExamplesAdapter(requireContext(), skillData.examples)
        } else {
            skillDetailExample.visibility = View.GONE
            skillDetailExamples.visibility = View.GONE
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

        // If the user is logged in, set up the five star skill rating bar
        if (PrefManager.token != null) {
            val map: MutableMap<String, String> = HashMap()
            map.put("model", skillData.model)
            map.put("group", skillData.group)
            map.put("language", skillData.language)
            map.put("skill", skillTag)
            map.put("access_token", PrefManager.token.toString())
            skillDetailsPresenter.updateUserRating(map)
            setUpFiveStarRatingBar()
        }

        // If the totalStar is positive, it implies that the skill has been rated
        // If so, set up the section to display the statistics else simply display a message for unrated skill
        if (skillData.skillRating != null) {
            if (skillData.skillRating?.stars != null) {
                if (skillData.skillRating?.stars?.totalStar as Int > 0) {

                    tvTotalRating.text = skillData.skillRating?.stars?.totalStar.toString()
                    tvAverageRating.text = skillData.skillRating?.stars?.averageStar.toString()
                    setSkillGraph()
                } else {
                    skill_rating_view.visibility = View.GONE
                    tv_unrated_skill.visibility = View.VISIBLE
                    if (PrefManager.token != null) {
                        tv_unrated_skill.text = getString(R.string.skill_unrated)
                    } else {
                        tv_unrated_skill.text = getString(R.string.skill_unrated_for_anonymous_user)
                        tv_unrated_skill.setOnClickListener {
                            val alertBuilder = AlertDialog.Builder(requireContext())
                            alertBuilder.setMessage(getString(R.string.skill_rate_login_alert))
                                    .setCancelable(false)
                                    .setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                                        dialog.cancel()
                                    })

                                    .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                                        loginLogoutModulePresenter.logout()
                                        val intent = Intent(requireContext(), LoginActivity::class.java)
                                        startActivity(intent)
                                    })

                            val alertPrompt = alertBuilder.create()
                            alertPrompt.setTitle("Rate Skills")
                            alertPrompt.show()
                        }
                    }
                }
            }
        } else {
            skill_rating_view.visibility = View.GONE
            tv_unrated_skill.visibility = View.VISIBLE
            if (PrefManager.token != null) {
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
        tvFiveStarSkillRatingBar.visibility = View.VISIBLE
        fiveStarSkillRatingBar.visibility = View.VISIBLE

        // Set up the OnRatingCarChange listener to change the rating scale text view contents accordingly
        fiveStarSkillRatingBar.setOnRatingBarChangeListener { ratingBar, v, fromUser ->

            tvFiveStarSkillRatingScale.visibility = View.VISIBLE

            if (fromUser == true) {
                this.fromUser = fromUser
            }

            val map: MutableMap<String, String> = HashMap()
            map.put("model", skillData.model)
            map.put("group", skillData.group)
            map.put("language", skillData.language)
            map.put("skill", skillTag)
            map.put("stars", v.toInt().toString())
            map.put("access_token", PrefManager.token.toString())
            skillDetailsPresenter.updateRatings(map)

            tvFiveStarSkillRatingScale.text = v.toString()
            when (ratingBar.rating.toInt()) {
                1 -> tvFiveStarSkillRatingScale.setText(R.string.rate_hate)
                2 -> tvFiveStarSkillRatingScale.setText(R.string.rate_improvement)
                3 -> tvFiveStarSkillRatingScale.setText(R.string.rate_good)
                4 -> tvFiveStarSkillRatingScale.setText(R.string.rate_great)
                5 -> tvFiveStarSkillRatingScale.setText(R.string.rate_awesome)
                else -> tvFiveStarSkillRatingScale.text = ""
            }
        }
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
            if (fromUser) {
                // Display a toast to notify the user that the rating has been submitted
                Toast.makeText(context, getString(R.string.toast_thank_for_rating), Toast.LENGTH_SHORT).show()
            }
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
        skillRatingChart.legend.isEnabled = false
        skillRatingChart.setPinchZoom(false)
        skillRatingChart.setDrawValueAboveBar(false)

        // Display the axis on the left (contains the labels 1*, 2* and so on)
        xAxis = skillRatingChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.position = XAxis.XAxisPosition.TOP
        xAxis.isEnabled = true

        val yLeft = skillRatingChart.axisLeft
        yLeft.axisMaximum = 100f
        yLeft.axisMinimum = 0f
        yLeft.isEnabled = false

        val yRight = skillRatingChart.axisRight
        yRight.setDrawAxisLine(true)
        yRight.setDrawGridLines(false)
        yRight.isEnabled = false

        // Set bar entries and add necessary formatting
        setData()

        // Add animation to the graph
        skillRatingChart.animateY(2000)
    }

    /**
     * Set the bar entries i.e. the percentage of users who rated the skill with
     * a certain number of stars.
     *
     * Set the colors for different bars and the bar width of the bars.
     */
    private fun setData() {

        val totalUsers: Int? = skillData.skillRating?.stars?.totalStar
        val oneStarUsers: Int? = skillData.skillRating?.stars?.oneStar
        val twoStarUsers: Int? = skillData.skillRating?.stars?.twoStar
        val threeStarUsers: Int? = skillData.skillRating?.stars?.threeStar
        val fourStarUsers: Int? = skillData.skillRating?.stars?.fourStar
        val fiveStarUsers: Int? = skillData.skillRating?.stars?.fiveStar

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

        // Set label count to 5 as we are using 5 star rating system
        xAxis.labelCount = 5
        xAxis.textColor = ContextCompat.getColor(skillRatingChart.context, R.color.md_grey_800)
        xAxis.valueFormatter = XAxisValueFormatter(values)

        // Add a list of bar entries
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, calcPercentageOfUsers(oneStarUsers, totalUsers)))
        entries.add(BarEntry(1f, calcPercentageOfUsers(twoStarUsers, totalUsers)))
        entries.add(BarEntry(2f, calcPercentageOfUsers(threeStarUsers, totalUsers)))
        entries.add(BarEntry(3f, calcPercentageOfUsers(fourStarUsers, totalUsers)))
        entries.add(BarEntry(4f, calcPercentageOfUsers(fiveStarUsers, totalUsers)))

        val barDataSet = BarDataSet(entries, "Bar Data Set")
        barDataSet.barShadowColor = Color.argb(40, 150, 150, 150)
        barDataSet.setDrawValues(false)

        // Set the colors for bars with first color for 1*, second for 2* and so on
        barDataSet.setColors(
                ContextCompat.getColor(skillRatingChart.context, R.color.md_red_300),
                ContextCompat.getColor(skillRatingChart.context, R.color.md_orange_300),
                ContextCompat.getColor(skillRatingChart.context, R.color.md_yellow_300),
                ContextCompat.getColor(skillRatingChart.context, R.color.md_light_green_300),
                ContextCompat.getColor(skillRatingChart.context, R.color.md_green_300)
        )

        val data = BarData(barDataSet)

        // Set the bar width
        // Note : To increase the spacing between the bars set the value of barWidth to < 1f
        data.barWidth = 0.9f
        skillRatingChart.setDrawBarShadow(true)

        // Finally set the data and refresh the graph
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
    private fun calcPercentageOfUsers(actualNumberOfUsers: Int?, totalNumberOfUsers: Int?): Float {
        return if (actualNumberOfUsers != null && totalNumberOfUsers != null) {
            (actualNumberOfUsers * 100f) / totalNumberOfUsers
        } else 0.1f
    }

    /**
     * Set up the feedback section
     *
     * If the user is logged in, show the post feedback section otherwise display an appropriate message
     */
    private fun setFeedback() {
        if (PrefManager.token != null) {
            tvPostFeedbackDesc.visibility = View.VISIBLE
            layoutPostFeedback.visibility = View.VISIBLE
            buttonPost.setOnClickListener {
                if (etFeedback.text?.trim().toString().isNotEmpty()) {
                    val queryObject = PostFeedback(skillData.model, skillData.group, skillData.language,
                            skillData.skillTag, etFeedback.text.toString(), PrefManager.token.toString())

                    skillDetailsPresenter.postFeedback(queryObject)
                } else {
                    Toast.makeText(context, getString(R.string.toast_empty_feedback), Toast.LENGTH_SHORT).show()
                }

                updateFeedback()
                setFeedback()
            }
        } else {
            tvAnonymousPostFeedback.visibility = View.VISIBLE
            tvAnonymousPostFeedback.setOnClickListener {
                loginLogoutModulePresenter.logout()
                val intentToLogin = Intent(context, LoginActivity::class.java)
                intentToLogin.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intentToLogin)
            }
        }

        val query = FetchFeedbackQuery(skillData.model, skillData.group, skillData.language, skillTag)
        skillDetailsPresenter.fetchFeedback(query)
    }

    /**
     * Displays a toast to show that the feedback has been posted successfully
     */
    override fun updateFeedback() {
        Toast.makeText(context, getString(R.string.toast_feedback_updated), Toast.LENGTH_SHORT).show()
        etFeedback.text?.clear()
        etFeedback.dispatchWindowFocusChanged(true)
        etFeedback.clearFocus()
        etFeedback.text?.clear()
        etFeedback.dispatchWindowFocusChanged(true)
        etFeedback.clearFocus()
    }

    /**
     * Displays the feedback list on the skill details screen
     *
     * @param feedbackResponse : Contains the list of Feedback objects received from the getSkillFeedback.json API
     */
    override fun updateFeedbackList(feedbackResponse: GetSkillFeedbackResponse) {
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rvFeedback.setHasFixedSize(true)
        rvFeedback.layoutManager = layoutManager
        rvFeedback.adapter = FeedbackAdapter(requireContext(), feedbackResponse)
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

    override fun updateSkillReportStatus(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
