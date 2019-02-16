package org.fossasia.susi.ai.skills.feedback

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.NavUtils
import android.support.v4.app.NotificationCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_feedback.rvAllFeedback
import kotlinx.android.synthetic.main.fragment_group_wise_skill_listing.*
import kotlinx.android.synthetic.main.item_skill_group_list.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.ChatActivity
import org.fossasia.susi.ai.dataclasses.GroupWiseSkills
import org.fossasia.susi.ai.device.deviceconnect.DeviceConnectFragment
import org.fossasia.susi.ai.rest.responses.susi.GetSkillFeedbackResponse
import org.fossasia.susi.ai.skills.SkillsActivity
import org.fossasia.susi.ai.skills.feedback.adapters.recycleradapters.AllReviewsAdapter
import org.fossasia.susi.ai.skills.groupwiseskills.GroupWiseSkillsFragment
import org.fossasia.susi.ai.skills.skilllisting.SkillListingFragment

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
        super.onBackPressed()
        finish()
    }
}
