package org.fossasia.susi.ai.login

import android.content.Context
import android.util.Patterns
import io.realm.Realm
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.CredentialHelper
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.helper.PrefManager.getStringSet
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.UnknownHostException

/**
 * Interactor/Model of Login
 * The M in MVP
 * Stores all business logic
 *
 * Created by chiragw15 on 4/7/17.
 */
class LoginInteractor: ILoginInteractor {

    var authResponseCall: Call<LoginResponse>?= null

    override fun login(email: String, password: String, isSusiServerSelected: Boolean, url: String, context: Context, listener: ILoginInteractor.OnLoginFinishedListener) {
        if (email.isEmpty()) {
            listener.emptyEmail()
            return
        }

        if(password.isEmpty()) {
            listener.emptyPassword()
            return
        }

        if (!CredentialHelper.isEmailValid(email)) {
            listener.incorrectEmail()
            return
        }

        if (!isSusiServerSelected) {
            if (url.isEmpty()){
                listener.emptyURL()
                return
            }

            if( Patterns.WEB_URL.matcher(url).matches()) {
                if (CredentialHelper.getValidURL(url) != null) {
                    PrefManager.putBoolean(Constant.SUSI_SERVER, false)
                    PrefManager.putString(Constant.CUSTOM_SERVER, CredentialHelper.getValidURL(url))
                } else {
                    listener.invalidURL()
                    return
                }
            } else {
                listener.invalidURL()
                return
            }
        } else {
            PrefManager.putBoolean(Constant.SUSI_SERVER, true)
        }

        makeLoginCall(email, password, context, listener)
    }

    override fun isLoggedIn(): Boolean {
        return !PrefManager.hasTokenExpired()
    }

    override fun isAnonymous(): Boolean {
        return PrefManager.getBoolean(Constant.ANONYMOUS_LOGGED_IN, false)
    }

    override fun getSavedEmails(): MutableSet<String>? {
        return getStringSet(Constant.SAVED_EMAIL)
    }

    override fun skipLogin(listener: ILoginInteractor.OnLoginSkipListener) {
        PrefManager.clearToken()
        PrefManager.putBoolean(Constant.ANONYMOUS_LOGGED_IN, true)
        listener.onSkip()
    }

    override fun cancelLogin() {
        authResponseCall?.cancel()
    }

    fun makeLoginCall(email: String, password: String, context: Context, listener: ILoginInteractor.OnLoginFinishedListener) {

        authResponseCall = ClientBuilder().susiApi
                .login(email.trim({ it <= ' ' }).toLowerCase(), password)

        listener.showProgress()

        authResponseCall?.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {

                    saveEmail(email)
                    saveToken(response)
                    deleteAllMessages()
                    PrefManager.putBoolean(Constant.ANONYMOUS_LOGGED_IN, false)

                    listener.onSuccess(response.body().message)
                } else if (response.code() == 422) {
                    listener.onError(context.getString(R.string.password_invalid_title),
                            context.getString(R.string.password_invalid))
                } else {
                    listener.onError("${response.code()} " + context.getString(R.string.error), response.message())
                }
                listener.hideProgress()
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                t.printStackTrace()
                if (t is UnknownHostException) {
                    listener.onError(context.getString(R.string.unknown_host_exception), t.message.toString())
                } else {
                    listener.onError(context.getString(R.string.error_internet_connectivity),
                            context.getString(R.string.no_internet_connection))
                }
                listener.hideProgress()
            }
        })
    }

    fun saveEmail(email: String) {
        val savedEmails = mutableSetOf<String>()
        if(PrefManager.getStringSet(Constant.SAVED_EMAIL) != null)
            savedEmails.addAll(PrefManager.getStringSet(Constant.SAVED_EMAIL))
        savedEmails.add(email)
        PrefManager.putStringSet(Constant.SAVED_EMAIL, savedEmails)
    }

    fun saveToken(response: Response<LoginResponse>) {
        PrefManager.putString(Constant.ACCESS_TOKEN, response.body().accessToken)
        val validity = System.currentTimeMillis() + response.body().validSeconds * 1000
        PrefManager.putLong(Constant.TOKEN_VALIDITY, validity)
    }

    fun deleteAllMessages() {
        val realm: Realm = Realm.getDefaultInstance()
        realm.executeTransaction({ realm ->
            realm.deleteAll()
        })
    }
}
