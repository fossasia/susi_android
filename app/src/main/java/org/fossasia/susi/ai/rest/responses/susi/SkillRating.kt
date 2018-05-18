package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import java.io.Serializable

/**
 *
 * Created by chiragw15 on 18/8/17.
 */
class SkillRating: Serializable {

    @Expose
    var positive: Int = 0


    @Expose
    var negative: Int = 0
}