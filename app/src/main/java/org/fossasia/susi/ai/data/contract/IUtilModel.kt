package org.fossasia.susi.ai.data.contract

import org.fossasia.susi.ai.rest.responses.susi.LoginResponse
import retrofit2.Response

/**
 * Interface for Util Model
 *
 * Created by chiragw15 on 10/7/17.
 */
interface IUtilModel {

    fun saveToken(response: Response<LoginResponse>)
    fun deleteAllMessages()
    fun saveAnonymity(isAnonymous: Boolean)
    fun getAnonymity(): Boolean
    fun saveEmail(email: String)
    fun getSavedEmails(): MutableSet<String>?
    fun isLoggedIn(): Boolean
    fun clearToken()
    fun setServer(isSusiServer: Boolean)
    fun setCustomURL(url: String)
    fun getString(id: Int): String
}