package org.fossasia.susi.ai.helper

import java.util.regex.Pattern

object ValidateUtils {
    private val VALID_URL_REGEX = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")

    fun validateUrl(urlStr: String): Boolean {
        val matcher = VALID_URL_REGEX.matcher(urlStr)
        return matcher.find()
    }
}
