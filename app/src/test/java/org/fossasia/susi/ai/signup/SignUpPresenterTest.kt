package org.fossasia.susi.ai.signup

import org.fossasia.susi.ai.data.SignUpModel
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.db.DatabaseRepository
import org.fossasia.susi.ai.data.db.contract.IDatabaseRepository
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.signup.contract.ISignUpView
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
class SignUpPresenterTest {

    @Mock
    val signUpView: ISignUpView = mock(SignUpActivity::class.java)
    @Mock
    val databaseRepository: IDatabaseRepository = mock(DatabaseRepository::class.java)
    @Mock
    val utilModel: UtilModel = mock(UtilModel::class.java)
    @Mock
    val iSignUpModel: SignUpModel = mock(SignUpModel::class.java)

    val signUpPresenter: SignUpPresenter = SignUpPresenter(iSignUpModel, utilModel, databaseRepository, signUpView)

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
}
