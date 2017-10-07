package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose

/**
 *
 * Created by chiragw15 on 16/8/17.
 */
class ListGroupsResponse {


    @Expose
    val groups: List<String> = ArrayList()
}