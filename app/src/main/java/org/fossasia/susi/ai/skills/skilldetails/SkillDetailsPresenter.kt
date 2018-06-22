package org.fossasia.susi.ai.skills.skilldetails

import org.fossasia.susi.ai.data.contract.ISkillDetailsModel
import org.fossasia.susi.ai.data.SkillDetailsModel
import org.fossasia.susi.ai.helper.NetworkUtils
import org.fossasia.susi.ai.rest.responses.susi.*
import org.fossasia.susi.ai.skills.skilldetails.contract.ISkillDetailsPresenter
import org.fossasia.susi.ai.skills.skilldetails.contract.ISkillDetailsView
import retrofit2.Response
import timber.log.Timber
import java.net.UnknownHostException

/**
 * Presenter for SkillDetails
 * The P in MVP
 *
 * @author arundhati24
 */
class SkillDetailsPresenter(skillDetailsFragment: SkillDetailsFragment) : ISkillDetailsPresenter,
        ISkillDetailsModel.OnUpdateRatingsFinishedListener, ISkillDetailsModel.OnUpdateUserRatingFinishedListener {

    private var skillDetailsModel: SkillDetailsModel = SkillDetailsModel()
    private var skillDetailsView: ISkillDetailsView? = null

    override fun onAttach(skillDetailsView: ISkillDetailsView) {
        this.skillDetailsView = skillDetailsView
    }

    override fun updateRatings(map: Map<String, String>) {
        skillDetailsModel.fiveStarRateSkill(map, this)
    }

    override fun cancelUpdateRatings() {
        skillDetailsModel.cancelUpdateRatings()
    }

    override fun updateUserRating(map: Map<String, String>) {
        skillDetailsModel.getRatingByUser(map, this)
    }

    override fun cancelUserRating() {
        skillDetailsModel.cancelUpdateUserRating()
    }

    override fun onError(throwable: Throwable) {

        if (throwable is UnknownHostException) {
            if (NetworkUtils.isNetworkConnected()) {
                Timber.e(throwable.toString())
            }
        }
    }

    override fun onUpdateUserRatingError(throwable: Throwable) {

        if (throwable is UnknownHostException) {
            if (NetworkUtils.isNetworkConnected()) {
                Timber.e(throwable.toString())
            }
        }
    }

    /**
     * Updates the stars object if the response if successful and the response is not null
     * with the help of the updateRatings method
     */
    override fun onSkillDetailsModelSuccess(response: Response<FiveStarSkillRatingResponse>) {

        if (response.isSuccessful && response.body() != null) {
            Timber.d(response.body().message)
            skillDetailsView?.updateRatings(response.body().ratings)
        } else {
            Timber.d("Could not update the ratings")
        }
    }

    override fun onUpdateUserRatingModelSuccess(response: Response<GetRatingByUserResponse>) {

        if (response.isSuccessful && response.body() != null) {
            Timber.d(response.body().message)
            skillDetailsView?.updateUserRating(response.body().ratings?.stars)
        } else {
            Timber.d("Could not update the user ratings")
        }
    }

    override fun onDetach() {
        skillDetailsView = null
    }
}
