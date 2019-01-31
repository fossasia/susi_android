package org.fossasia.susi.ai.skills.feedback

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_feedback.rvAllFeedback
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.rest.responses.susi.GetSkillFeedbackResponse
import org.fossasia.susi.ai.skills.feedback.adapters.recycleradapters.AllReviewsAdapter

/**
 *
 * Created by arundhati24 on 27/06/2018
 */
class FeedbackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        val feedbackResponse = intent.extras.get("feedbackResponse") as GetSkillFeedbackResponse
        if (feedbackResponse != null) {
            title = feedbackResponse.skillName.capitalize() + " " + getString(R.string.reviews)
            if (feedbackResponse.feedbackList != null) {
                val mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                rvAllFeedback.setHasFixedSize(true)
                rvAllFeedback.layoutManager = mLayoutManager
                rvAllFeedback.adapter = AllReviewsAdapter(this, feedbackResponse.feedbackList)
            }
        }
    }

    override fun onBackPressed() {
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
        finish()
    }
}