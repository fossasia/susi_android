package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * <h1>Kotlin Data class to parse retrofit response from forgot password endpoint susi client.</h1>
 */

data class ForgotPasswordResponse (

    val message: String? = null
)
