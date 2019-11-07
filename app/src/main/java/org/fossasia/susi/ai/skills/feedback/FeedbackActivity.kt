package org.fossasia.susi.ai.skills.feedback

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_feedback.rvAllFeedback
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.rest.responses.susi.Feedback
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
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        val feedbackResponse: GetSkillFeedbackResponse? = intent.extras.get("feedbackResponse") as GetSkillFeedbackResponse
        val arrangedFeedbackList: ArrayList<Feedback> = intent.extras.get("arrangedFeedbackList") as ArrayList<Feedback>
        if (feedbackResponse != null) {
            title = feedbackResponse.skillName.capitalize() + " " + getString(R.string.reviews)
            if (!arrangedFeedbackList.isEmpty()) {
                val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                rvAllFeedback.setHasFixedSize(true)
                rvAllFeedback.layoutManager = layoutManager
                rvAllFeedback.adapter = AllReviewsAdapter(this, arrangedFeedbackList)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
        super.onBackPressed()
        finish()
    }
}
