package org.fossasia.susi.ai;

import org.fossasia.susi.ai.data.LoginModel;
import org.fossasia.susi.ai.data.UtilModel;
import org.fossasia.susi.ai.data.contract.ILoginModel;
import org.fossasia.susi.ai.data.contract.IUtilModel;
import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.login.LoginActivity;
import org.fossasia.susi.ai.login.LoginPresenter;
import org.fossasia.susi.ai.login.contract.ILoginView;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

/**
 * Created by meeera on 2/8/17.
 */

@RunWith(JUnit4.class)
public class LoginTestPresenter {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    PrefManager prefManager;

    UtilModel utilModel;
    LoginModel loginModel;
    @Mock
    IUtilModel iutilModel;
    @Mock
    ILoginView iLoginView;
    @Mock
     MainApplication mainApplication;

    LoginActivity loginActivity;

    @Mock
    ILoginModel.OnLoginFinishedListener loginFinishedListener;

    @Mock
    ILoginModel iLoginModel;

    private LoginPresenter loginPresenter;

    private String email = "singhalsaurabh95@gmail.com";
    private String password = "qwertY12";

    @Before
    public void setUp() {
        loginActivity = new LoginActivity();
        loginPresenter = new LoginPresenter(loginActivity);
        loginModel = new LoginModel();
        utilModel = new UtilModel(loginActivity);
        loginPresenter.onAttach(iLoginView);

    }

    @Test
    public void shouldShowSuccessOnStart() {
        Mockito.when(iutilModel.isLoggedIn()).thenReturn(true);
        loginPresenter.onStart();
        Mockito.verify(iLoginView).skipLogin();
    }

    @Test
    public void shouldNotShowSuccessOnStart() {
        Mockito.when(iutilModel.isLoggedIn()).thenReturn(false);
        loginPresenter.onStart();
        Mockito.verify(iLoginView, Mockito.never()).skipLogin();
    }

    @Test
    public void shouldLoginAutomatically() {
        Mockito.when(iutilModel.isLoggedIn()).thenReturn(true);

        loginPresenter.onStart();

        Mockito.verify(iLoginView).skipLogin();
    }

    @Test
    public void shouldNotLoginAutomatically() {
        Mockito.when(iutilModel.isLoggedIn()).thenReturn(false);

        loginPresenter.onStart();

        Mockito.verify(iLoginView, Mockito.never()).skipLogin();
    }

    @Test
    public void shouldLoginSuccessfully() {
        doNothing().when(iLoginModel).login(email,password,loginFinishedListener);
        InOrder inOrder = Mockito.inOrder(iLoginModel,iLoginView);
        loginPresenter.login(email,password,true,"test");
        inOrder.verify(iLoginModel).login(email,password,loginFinishedListener);
        inOrder.verify(iLoginView).onLoginSuccess("yes");
       // Mockito.when(loginModel.login(email,password,loginFinishedListener));
    }
}
