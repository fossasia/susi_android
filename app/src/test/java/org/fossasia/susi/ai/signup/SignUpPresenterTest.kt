package org.fossasia.susi.ai.signup

import android.graphics.Color
import okhttp3.ResponseBody
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.ForgotPasswordModel
import org.fossasia.susi.ai.data.SignUpModel
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.db.DatabaseRepository
import org.fossasia.susi.ai.data.db.contract.IDatabaseRepository
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.rest.responses.susi.ForgotPasswordResponse
import org.fossasia.susi.ai.rest.responses.susi.SignUpResponse
import org.fossasia.susi.ai.signup.contract.ISignUpView
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
class SignUpPresenterTest {
    private val ERROR_INTERNET_CONNECTIVITY = "Internet Connectivity Problem."
    private val NO_INTERNET_AVAILABLE = "Internet Connection Not Available."
    private val UNKNOWN_HOST_EXPRESSINON = "Unknown Host Exception"

    private val responseSuccess: retrofit2.Response<SignUpResponse> = retrofit2.Response.success(SignUpResponse("Message", null))
    private val responseCode422: retrofit2.Response<SignUpResponse> = retrofit2.Response.error(422, ResponseBody.create(null, "content"))
    private val responseCodeUnknown: retrofit2.Response<SignUpResponse> = retrofit2.Response.error(500, ResponseBody.create(null, "content"))

    private val forgotResponseSuccess: retrofit2.Response<ForgotPasswordResponse> = retrofit2.Response.success(ForgotPasswordResponse("Message"))
    private val forgotResponse422: retrofit2.Response<ForgotPasswordResponse> = retrofit2.Response.error(422, ResponseBody.create(null, "content"))
    private val forgotResponseUnknown: retrofit2.Response<ForgotPasswordResponse> = retrofit2.Response.error(500, ResponseBody.create(null, "content"))

    @Mock
    val signUpView: ISignUpView = mock(SignUpActivity::class.java)
    @Mock
    val databaseRepository: IDatabaseRepository = mock(DatabaseRepository::class.java)
    @Mock
    val utilModel: UtilModel = mock(UtilModel::class.java)
    @Mock
    val iSignUpModel: SignUpModel = mock(SignUpModel::class.java)
    @Mock
    val forgotPasswordModel: ForgotPasswordModel = mock(ForgotPasswordModel::class.java)

    val signUpPresenter: SignUpPresenter = SignUpPresenter(forgotPasswordModel, iSignUpModel, utilModel, databaseRepository, signUpView)

    @Before
    fun setUp() {
        `when`(utilModel.getString(R.string.error_internet_connectivity)).thenReturn(ERROR_INTERNET_CONNECTIVITY)
        `when`(utilModel.getString(R.string.no_internet_connection)).thenReturn(NO_INTERNET_AVAILABLE)
        `when`(utilModel.getString(R.string.unknown_host_exception)).thenReturn(UNKNOWN_HOST_EXPRESSINON)
    }

    @Test
    fun signUpCredentialsTest() {
        signUpPresenter.signUp("", "", "", true, "", false)
        verify(signUpView).invalidCredentials(true, Constant.EMAIL)

        signUpPresenter.signUp("email", "", "", true, "", false)
        verify(signUpView).invalidCredentials(true, Constant.PASSWORD)

        signUpPresenter.signUp("email", "password", "", true, "", false)
        verify(signUpView).invalidCredentials(true, Constant.CONFIRM_PASSWORD)

        signUpPresenter.signUp("email", "password", "pass", true, "", false)
        verify(signUpView).invalidCredentials(true, Constant.ACCEPT_TERMS_AND_CONDITIONS)

        signUpPresenter.signUp("email", "password", "pass", true, "", true)
        verify(signUpView).invalidCredentials(false, Constant.EMAIL)

        signUpPresenter.signUp("email@gmail.com", "password", "pass", true, "", true)
        verify(signUpView).passwordInvalid()

        signUpPresenter.signUp("email@gmail.com", "P@ssword12", "P@ssword", true, "", true)
        verify(signUpView).invalidCredentials(false, Constant.PASSWORD)

        signUpPresenter.signUp("email@gmail.com", "P@ssword12", "P@ssword12", true, "", true)
        verify(signUpView).invalidCredentials(false, Constant.PASSWORD)

        signUpPresenter.signUp("email@gmail.com", "P@ssword12", "P@ssword12", false, "", true)
        verify(signUpView).invalidCredentials(false, Constant.INPUT_URL)

        signUpPresenter.signUp("email@gmail.com", "P@ssword12", "P@ssword12", false, "github", true)
        verify(signUpView, times(2)).invalidCredentials(false, Constant.INPUT_URL)

        signUpPresenter.signUp("email@gmail.com", "P@ssword12", "P@ssword12", false, "https://github.com", true)
        verify(signUpView, times(2)).invalidCredentials(false, Constant.INPUT_URL)
    }

    @Test
    fun shouldSignUpSuccessfully_onSusiServer() {
        signUpPresenter.signUp("email@gmail.com", "P@ssword12", "P@ssword12", true, "", true)
        verify(signUpView).showProgress(true)
        verify(iSignUpModel).signUp("email@gmail.com", "P@ssword12", signUpPresenter)
    }

    @Test
    fun shouldSignUpSuccessfully_onCustomServer() {
        signUpPresenter.signUp("email@gmail.com", "P@ssword12", "P@ssword12", false, "https://github.com", true)
        verify(signUpView).showProgress(true)
        verify(iSignUpModel).signUp("email@gmail.com", "P@ssword12", signUpPresenter)
    }

    @Test
    fun shouldShowErrorOnFailure() {
        signUpPresenter.onError(Throwable())
        verify(signUpView).showProgress(false)
        verify(signUpView).onSignUpError(utilModel.getString(R.string.error_internet_connectivity),
                utilModel.getString(R.string.no_internet_connection))
    }

    @Test
    fun shouldShowSuccessOnSuccess() {
        signUpPresenter.onSuccess(responseSuccess)
        verify(signUpView, times(2)).showProgress(false)
        verify(signUpView).alertSuccess()
    }

    @Test
    fun shouldShowErrorOnResponse422() {
        signUpPresenter.onSuccess(responseCode422)
        verify(signUpView, times(2)).showProgress(false)
        verify(signUpView).alertFailure()
    }

    @Test
    fun shouldShowSignUpError_onUnknownResponseCode() {
        signUpPresenter.onSuccess(responseCodeUnknown)
        verify(signUpView, times(2)).showProgress(false)
        verify(signUpView).onSignUpError("${responseCodeUnknown.code()} " + utilModel.getString(R.string.error), responseCodeUnknown.message())
    }

    @Test
    fun requestPasswordTest() {
        signUpPresenter.requestPassword("", "", true)
        verify(signUpView).invalidCredentials(true, Constant.EMAIL)

        signUpPresenter.requestPassword("email", "", true)
        verify(signUpView).invalidCredentials(false, Constant.EMAIL)

        signUpPresenter.requestPassword("email@email.com", "", true)
        verify(signUpView).invalidCredentials(true, Constant.INPUT_URL)

        signUpPresenter.requestPassword("email@email.com", "url", true)
        verify(signUpView).invalidCredentials(false, Constant.INPUT_URL)

        signUpPresenter.requestPassword("email@email.com", "https://github.com", true)
        verify(utilModel).setServer(false)
        verify(utilModel).setCustomURL("https://github.com")
        verify(signUpView).showForgotPasswordProgress(true)
        verify(forgotPasswordModel).requestPassword("email@email.com", signUpPresenter)

        signUpPresenter.requestPassword("email@email.com", "https://github.com", false)
        utilModel.setServer(true)
        verify(signUpView, times(2)).showForgotPasswordProgress(true)
        verify(forgotPasswordModel, times(2)).requestPassword("email@email.com", signUpPresenter)
    }

    @Test
    fun forgotResponseSuccessTest() {
        signUpPresenter.onForgotPasswordModelSuccess(forgotResponseSuccess)
        verify(signUpView).showForgotPasswordProgress(false)
        verify(signUpView).resetPasswordSuccess()
    }

    @Test
    fun forgotResponseCode422() {
        signUpPresenter.onForgotPasswordModelSuccess(forgotResponse422)
        verify(signUpView).resetPasswordFailure(utilModel.getString(R.string.email_invalid_title), utilModel.getString(R.string.email_invalid), utilModel.getString(R.string.retry), Color.RED)
        verify(signUpView).showForgotPasswordProgress(false)
    }

    @Test
    fun forgotResponseUnknown() {
        signUpPresenter.onForgotPasswordModelSuccess(forgotResponseUnknown)
        verify(signUpView).showForgotPasswordProgress(false)
        verify(signUpView).resetPasswordFailure("${forgotResponseUnknown.code()} " + utilModel.getString(R.string.error), forgotResponseUnknown.message(), utilModel.getString(R.string.ok), Color.BLUE)
    }
}
