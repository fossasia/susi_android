package org.fossasia.susi.ai.skills.skilldetails.contract

import org.fossasia.susi.ai.dataclasses.FetchFeedbackQuery
import org.fossasia.susi.ai.dataclasses.PostFeedback
import org.fossasia.susi.ai.dataclasses.ReportSkillQuery

/**
 * The interface for SkillDetails Presenter
 *
 * @author arundhati24
 */
interface ISkillDetailsPresenter {

    fun updateRatings(map: Map<String, String>)

    fun cancelUpdateRatings()

    fun updateUserRating(map: Map<String, String>)

    fun cancelUserRating()

    fun sendReport(queryObject: ReportSkillQuery)

    fun postFeedback(queryObject: PostFeedback)

    fun cancelPostFeedback()

    fun fetchFeedback(query: FetchFeedbackQuery)

    fun cancelFetchFeedback()
}
