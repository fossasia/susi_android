package org.fossasia.susi.ai.skills.skilldetails.contract

import android.media.Rating
import org.fossasia.susi.ai.rest.responses.susi.Ratings
import org.fossasia.susi.ai.rest.responses.susi.Stars

/**
 * The interface for SkillDetails View
 *
 * @author arundhati24
 */
interface ISkillDetailsView {

    fun updateRatings(ratingsObject: Stars?)

    fun updateUserRating(updatedRating: Int?)
}