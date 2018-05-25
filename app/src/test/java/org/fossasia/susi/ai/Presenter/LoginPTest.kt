package org.fossasia.susi.ai.Presenter

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.fossasia.susi.ai.login.LoginActivity
import org.fossasia.susi.ai.login.LoginPresenter
import org.fossasia.susi.ai.login.contract.ILoginView
import org.junit.Test

class LoginPTest {

    @Test
    fun should_Show_Success_onStart() {
        val presenter = LoginPresenter(loginActivity = LoginActivity())

        val view: ILoginView = mock()
        presenter.onAttach(view)

        var email = "test"
        var password = "test"
        var url = "test"
        var serverCon = false

        presenter.login(email,password,serverCon,url)

        verify(view).onLoginSuccess("")
    }
}