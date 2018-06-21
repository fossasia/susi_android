package org.fossasia.susi.ai.data.contract

import org.fossasia.susi.ai.rest.responses.susi.FiveStarSkillRatingResponse
import retrofit2.Response

/**
 * The interface for SkillDetails Model
 *
 * @author arundhati24
 */
interface ISkillDetailsModel {

    interface OnUpdateRatingsFinishedListener {
        fun onError(throwable: Throwable)
        fun onSkillDetailsModelSuccess(response: Response<FiveStarSkillRatingResponse>)
    }

    fun fiveStarRateSkill(map: Map<String, String>, listener: OnUpdateRatingsFinishedListener)

    fun cancelUpdateRatings()

}
