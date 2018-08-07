package org.fossasia.susi.ai.rest.responses.susi


/**
 * Created by meeera on 30/6/17.
 */

data class ChangeSettingResponse(
        /**
         * Gets session
         *
         * @return the session
         */
        val session: Session? = null,

        /**
         * Gets message
         *
         * @return the message
         */
        val message: String? = null
)
