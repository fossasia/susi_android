package org.fossasia.susi.ai.skills.skilldetails

import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.contract.ISkillDetailsModel
import org.fossasia.susi.ai.data.SkillDetailsModel
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.dataclasses.FetchFeedbackQuery
import org.fossasia.susi.ai.dataclasses.PostFeedback
import org.fossasia.susi.ai.dataclasses.ReportSkillQuery
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
        ISkillDetailsModel.OnUpdateRatingsFinishedListener, ISkillDetailsModel.OnUpdateUserRatingFinishedListener,
        ISkillDetailsModel.OnUpdateFeedbackFinishedListener, ISkillDetailsModel.OnFetchFeedbackFinishedListener,
        ISkillDetailsModel.OnReportSendListener {

    private var skillDetailsModel: SkillDetailsModel = SkillDetailsModel()
    private var skillDetailsView: ISkillDetailsView? = null
    private val utilModel: UtilModel = UtilModel(skillDetailsFragment.requireContext())

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

    override fun postFeedback(queryObject: PostFeedback) {
        skillDetailsModel.postFeedback(queryObject, this)
    }

    override fun cancelPostFeedback() {
        skillDetailsModel.cancelPostFeedback()
    }

    override fun fetchFeedback(query: FetchFeedbackQuery) {
        skillDetailsModel.fetchFeedback(query, this)
    }

    override fun cancelFetchFeedback() {
        skillDetailsModel.cancelFetchFeedback()
    }

    override fun onError(throwable: Throwable) {

        if (throwable is UnknownHostException) {
            if (NetworkUtils.isNetworkConnected()) {
                Timber.e(throwable.toString())
            }
        }
    }

    override fun sendReport(queryObject: ReportSkillQuery) {
        skillDetailsModel.sendReport(queryObject, this)
    }

    override fun onUpdateUserRatingError(throwable: Throwable) {

        if (throwable is UnknownHostException) {
            if (NetworkUtils.isNetworkConnected()) {
                Timber.e(throwable.toString())
            }
        }
    }

    override fun onUpdateFeedbackError(throwable: Throwable) {
        if (throwable is UnknownHostException) {
            if (NetworkUtils.isNetworkConnected()) {
                Timber.e(throwable.toString())
            }
        }
    }

    override fun onFetchFeedbackError(throwable: Throwable) {
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
        val res = response.body()
        if (response.isSuccessful && res != null) {
            Timber.d(res.message)
            skillDetailsView?.updateRatings(res.ratings)
        } else {
            Timber.d("Could not update the ratings")
        }
    }

    override fun onUpdateUserRatingModelSuccess(response: Response<GetRatingByUserResponse>) {
        val res = response.body()
        if (response.isSuccessful && res != null) {
            Timber.d(res.message)
            skillDetailsView?.updateUserRating(res.ratings?.stars)
        } else {
            Timber.d("Could not update the user ratings")
        }
    }

    override fun onUpdateFeedbackModelSuccess(response: Response<PostSkillFeedbackResponse>) {
        val res = response.body()
        if (response.isSuccessful && res != null) {
            Timber.d(res.message)
            skillDetailsView?.updateFeedback()
        } else {
            Timber.d("Could not update feedback")
        }
    }

    override fun onFetchFeedbackModelSuccess(response: Response<GetSkillFeedbackResponse>) {
        val res = response.body()
        if (response.isSuccessful && res != null) {
            Timber.d(res.message)
            skillDetailsView?.updateFeedbackList(res)
        } else {
            Timber.d("Could not fetch feedback")
        }
    }

    override fun reportSendError(throwable: Throwable) {
        Timber.e(throwable)
        skillDetailsView?.updateSkillReportStatus(utilModel.getString(R.string.report_error))
    }

    override fun reportSendSuccess(response: Response<ReportSkillResponse>) {
        if (response.code() == 422) {
            skillDetailsView?.updateSkillReportStatus(utilModel.getString(R.string.reported_already))
        } else {
            skillDetailsView?.updateSkillReportStatus(utilModel.getString(R.string.report_send_success))
        }
    }

    override fun onDetach() {
        skillDetailsView = null
    }
}