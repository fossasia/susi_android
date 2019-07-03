package org.fossasia.susi.ai.skills.feedback

import android.content.Intent
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
    companion object {
        val FEEDBACK_DELETION = 2
        val FEEDBACK_DELETED = 1
        var FEEDBACK_DELETION_STATUS = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        val feedbackResponse: GetSkillFeedbackResponse? = intent.extras.get("feedbackResponse") as GetSkillFeedbackResponse
        val skillLanguage = intent.getStringExtra("skillLanguage")
        val skillGroup = intent.getStringExtra("skillGroup")
        val skillModel = intent.getStringExtra("skillModel")
        val arrangedFeedbackList: ArrayList<Feedback> = intent.extras.get("arrangedFeedbackList") as ArrayList<Feedback>
        if (feedbackResponse != null) {
            title = feedbackResponse.skillName.capitalize() + " " + getString(R.string.reviews)
            if (!arrangedFeedbackList.isEmpty()) {
                val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                rvAllFeedback.setHasFixedSize(true)
                rvAllFeedback.layoutManager = layoutManager
                rvAllFeedback.adapter = AllReviewsAdapter(this, arrangedFeedbackList, feedbackResponse.skillName, skillLanguage, skillGroup, skillModel)
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
        val intent = Intent()
        setResult(FEEDBACK_DELETION_STATUS, intent)
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
        super.onBackPressed()
        finish()
    }
}
