package org.fossasia.susi.ai.data.contract

import org.fossasia.susi.ai.rest.responses.susi.ListSkillsResponse
import retrofit2.Response

/**
 *
 * Created by arundhati24 on 16/07/2018.
 */
interface IGroupWiseSkillsModel {
    interface OnFetchSkillsFinishedListener {
        fun onSkillFetchSuccess(response: Response<ListSkillsResponse>, group: String)
        fun onSkillFetchFailure(t: Throwable)
    }

    fun fetchSkills(group: String, language: String, filter_name: String, filter_type: String, duration: String)

    fun cancelFetch()
}
