package org.fossasia.susi.ai.data

import kotlin.collections.HashMap
import org.fossasia.susi.ai.data.contract.ISkillDetailsModel
import org.fossasia.susi.ai.dataclasses.FetchFeedbackQuery
import org.fossasia.susi.ai.dataclasses.PostFeedback
import org.fossasia.susi.ai.dataclasses.ReportSkillQuery
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.FiveStarSkillRatingResponse
import org.fossasia.susi.ai.rest.responses.susi.GetRatingByUserResponse
import org.fossasia.susi.ai.rest.responses.susi.GetSkillFeedbackResponse
import org.fossasia.susi.ai.rest.responses.susi.PostSkillFeedbackResponse
import org.fossasia.susi.ai.rest.responses.susi.ReportSkillResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

/**
 * Model of SkillDetails
 * The M in MVP
 * Stores all business logic
 *
 * @author arundhati24
 *
 */

class SkillDetailsModel : ISkillDetailsModel {

    private lateinit var updateRatingsResponseCall: Call<FiveStarSkillRatingResponse>
    private lateinit var updateUserRatingResponseCall: Call<GetRatingByUserResponse>
    private lateinit var updateFeedbackResponseCall: Call<PostSkillFeedbackResponse>
    private lateinit var fetchFeedbackResponseCall: Call<GetSkillFeedbackResponse>
    private lateinit var reportSkillResponseCall: Call<ReportSkillResponse>

    /**
     * Posts a request the fiveStarRateSkill.json API
     *
     * @param map A query map consisting of following key value pairs
     *             model       Model of the skill (e.g. general)
     *             group       Group of skill (e.g. Knowledge)
     *             language    Language directory in which the skill resides (e.g. en)
     *             skill       Skill Tag of object in which the skill data resides
     *             rating      User rating to be sent to the server
     *             accessToken Access token of logged in user
     *             listener
     *
     */
    override fun fiveStarRateSkill(map: Map<String, String>, listener: ISkillDetailsModel.OnUpdateRatingsFinishedListener) {

        updateRatingsResponseCall = ClientBuilder.susiApi.fiveStarRateSkill(map)

        updateRatingsResponseCall.enqueue(object : Callback<FiveStarSkillRatingResponse> {
            override fun onResponse(call: Call<FiveStarSkillRatingResponse>, response: Response<FiveStarSkillRatingResponse>) {
                listener.onSkillDetailsModelSuccess(response)
            }

            override fun onFailure(call: Call<FiveStarSkillRatingResponse>, t: Throwable) {
                Timber.e(t)
                listener.onError(t)
            }
        })
    }

    override fun getRatingByUser(map: Map<String, String>, listener: ISkillDetailsModel.OnUpdateUserRatingFinishedListener) {
        updateUserRatingResponseCall = ClientBuilder.susiApi.getRatingByUser(map)

        updateUserRatingResponseCall.enqueue(object : Callback<GetRatingByUserResponse> {
            override fun onResponse(call: Call<GetRatingByUserResponse>, response: Response<GetRatingByUserResponse>) {
                listener.onUpdateUserRatingModelSuccess(response)
            }

            override fun onFailure(call: Call<GetRatingByUserResponse>, t: Throwable) {
                Timber.e(t)
                listener.onUpdateUserRatingError(t)
            }
        })
    }

    override fun sendReport(query: ReportSkillQuery, listener: ISkillDetailsModel.OnReportSendListener) {
        reportSkillResponseCall = ClientBuilder.sendReportCall(query)

        reportSkillResponseCall.enqueue(object : Callback<ReportSkillResponse> {
            override fun onResponse(call: Call<ReportSkillResponse>?, response: Response<ReportSkillResponse>) {
                listener.reportSendSuccess(response)
            }

            override fun onFailure(call: Call<ReportSkillResponse>?, t: Throwable) {
                Timber.e(t)
                listener.reportSendError(t)
            }
        })
    }

    override fun postFeedback(queryObject: PostFeedback, listener: ISkillDetailsModel.OnUpdateFeedbackFinishedListener) {
        val query: MutableMap<String, String> = HashMap()
        query.put("model", queryObject.model)
        query.put("group", queryObject.group)
        query.put("language", queryObject.language)
        query.put("skill", queryObject.skill)
        query.put("feedback", queryObject.feedback)
        query.put("access_token", queryObject.accessToken)
        updateFeedbackResponseCall = ClientBuilder.susiApi.postFeedback(query)

        updateFeedbackResponseCall.enqueue(object : Callback<PostSkillFeedbackResponse> {
            override fun onResponse(call: Call<PostSkillFeedbackResponse>, response: Response<PostSkillFeedbackResponse>) {
                listener.onUpdateFeedbackModelSuccess(response)
            }

            override fun onFailure(call: Call<PostSkillFeedbackResponse>, t: Throwable) {
                Timber.e(t)
                listener.onUpdateFeedbackError(t)
            }
        })
    }

    override fun fetchFeedback(query: FetchFeedbackQuery, listener: ISkillDetailsModel.OnFetchFeedbackFinishedListener) {

        fetchFeedbackResponseCall = ClientBuilder.fetchFeedbackCall(query)

        fetchFeedbackResponseCall.enqueue(object : Callback<GetSkillFeedbackResponse> {
            override fun onResponse(call: Call<GetSkillFeedbackResponse>, response: Response<GetSkillFeedbackResponse>) {
                listener.onFetchFeedbackModelSuccess(response)
            }

            override fun onFailure(call: Call<GetSkillFeedbackResponse>, t: Throwable) {
                Timber.e(t)
                listener.onFetchFeedbackError(t)
            }
        })
    }

    override fun cancelUpdateRatings() {
        updateRatingsResponseCall.cancel()
    }

    override fun cancelUpdateUserRating() {
        updateUserRatingResponseCall.cancel()
    }

    override fun cancelPostFeedback() {
        updateFeedbackResponseCall.cancel()
    }

    override fun cancelFetchFeedback() {
        fetchFeedbackResponseCall.cancel()
    }
}
