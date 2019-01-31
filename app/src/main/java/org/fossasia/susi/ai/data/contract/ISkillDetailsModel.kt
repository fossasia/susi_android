package org.fossasia.susi.ai.data.contract

import org.fossasia.susi.ai.dataclasses.FetchFeedbackQuery
import org.fossasia.susi.ai.dataclasses.PostFeedback
import org.fossasia.susi.ai.dataclasses.ReportSkillQuery
import org.fossasia.susi.ai.rest.responses.susi.FiveStarSkillRatingResponse
import org.fossasia.susi.ai.rest.responses.susi.GetRatingByUserResponse
import org.fossasia.susi.ai.rest.responses.susi.GetSkillFeedbackResponse
import org.fossasia.susi.ai.rest.responses.susi.PostSkillFeedbackResponse
import org.fossasia.susi.ai.rest.responses.susi.ReportSkillResponse
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

    interface OnUpdateFeedbackFinishedListener {
        fun onUpdateFeedbackError(throwable: Throwable)
        fun onUpdateFeedbackModelSuccess(response: Response<PostSkillFeedbackResponse>)
    }

    interface OnFetchFeedbackFinishedListener {
        fun onFetchFeedbackError(throwable: Throwable)
        fun onFetchFeedbackModelSuccess(response: Response<GetSkillFeedbackResponse>)
    }

    interface OnReportSendListener {
        fun reportSendError(throwable: Throwable)
        fun reportSendSuccess(response: Response<ReportSkillResponse>)
    }

    fun fiveStarRateSkill(map: Map<String, String>, listener: OnUpdateRatingsFinishedListener)

    fun cancelUpdateRatings()

    fun getRatingByUser(map: Map<String, String>, listener: OnUpdateUserRatingFinishedListener)

    fun cancelUpdateUserRating()

    fun postFeedback(query: PostFeedback, listener: OnUpdateFeedbackFinishedListener)

    fun cancelPostFeedback()

    fun fetchFeedback(query: FetchFeedbackQuery, listener: OnFetchFeedbackFinishedListener)

    fun cancelFetchFeedback()

    fun sendReport(query: ReportSkillQuery, listener: OnReportSendListener)
}
