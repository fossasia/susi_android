package org.fossasia.susi.ai.skills.skilldetails.contract

import org.fossasia.susi.ai.dataclasses.FetchFeedbackQuery
import org.fossasia.susi.ai.dataclasses.PostFeedback

/**
 * The interface for SkillDetails Presenter
 *
 * @author arundhati24
 */
interface ISkillDetailsPresenter {

    fun onAttach(skillDetailsView: ISkillDetailsView)

    fun updateRatings(map: Map<String, String>)

    fun cancelUpdateRatings()

    fun updateUserRating(map: Map<String, String>)

    fun cancelUserRating()

    fun postFeedback(queryObject: PostFeedback)

    fun cancelPostFeedback()

    fun fetchFeedback(query: FetchFeedbackQuery)

    fun cancelFetchFeedback()

    fun onDetach()
}
