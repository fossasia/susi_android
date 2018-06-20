package org.fossasia.susi.ai.data.contract

import org.fossasia.susi.ai.rest.responses.susi.FiveStarSkillRatingResponse
import org.fossasia.susi.ai.rest.responses.susi.GetRatingByUserResponse
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

    interface OnUpdateUserRatingFinishedListener {
        fun onUpdateUserRatingError(throwable: Throwable)
        fun onUpdateUserRatingModelSuccess(response: Response<GetRatingByUserResponse>)
    }

    fun fiveStarRateSkill(map: Map<String, String>, listener: OnUpdateRatingsFinishedListener)

    fun cancelUpdateRatings()

    fun getRatingByUser(map: Map<String, String>, listener: OnUpdateUserRatingFinishedListener)

    fun cancelUpdateUserRating()
}
