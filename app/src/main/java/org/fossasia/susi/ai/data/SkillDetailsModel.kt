package org.fossasia.susi.ai.data

import org.fossasia.susi.ai.data.contract.ISkillDetailsModel
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.FiveStarSkillRatingResponse
import org.fossasia.susi.ai.rest.responses.susi.GetRatingByUserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    /**
     * Posts a request the fiveStarRateSkill.json API
     *
     * @param map  A query map consisting of following key value pairs
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

        updateRatingsResponseCall = ClientBuilder().susiApi.fiveStarRateSkill(map)

        updateRatingsResponseCall.enqueue(object : Callback<FiveStarSkillRatingResponse> {
            override fun onResponse(call: Call<FiveStarSkillRatingResponse>, response: Response<FiveStarSkillRatingResponse>) {
                listener.onSkillDetailsModelSuccess(response)
            }

            override fun onFailure(call: Call<FiveStarSkillRatingResponse>, t: Throwable) {
                t.printStackTrace()
                listener.onError(t)
            }
        })
    }

    override fun getRatingByUser(map: Map<String, String>, listener: ISkillDetailsModel.OnUpdateUserRatingFinishedListener) {
        updateUserRatingResponseCall = ClientBuilder().susiApi.getRatingByUser(map)

        updateUserRatingResponseCall.enqueue(object : Callback<GetRatingByUserResponse> {
            override fun onResponse(call: Call<GetRatingByUserResponse>, response: Response<GetRatingByUserResponse>) {
                listener.onUpdateUserRatingModelSuccess(response)
            }

            override fun onFailure(call: Call<GetRatingByUserResponse>, t: Throwable) {
                t.printStackTrace()
                listener.onUpdateUserRatingError(t)
            }
        })
    }

    override fun cancelUpdateRatings() {
        updateRatingsResponseCall.cancel()
    }

    override fun cancelUpdateUserRating() {
        updateUserRatingResponseCall.cancel()
    }

}
