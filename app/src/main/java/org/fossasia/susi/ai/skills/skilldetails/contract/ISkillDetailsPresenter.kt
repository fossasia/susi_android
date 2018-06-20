package org.fossasia.susi.ai.skills.skilldetails.contract

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

    fun onDetach()
}
