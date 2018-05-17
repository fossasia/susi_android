package org.fossasia.susi.ai.Presenter;

import org.fossasia.susi.ai.data.UtilModel;
import org.fossasia.susi.ai.data.contract.ILoginModel;
import org.fossasia.susi.ai.data.db.DatabaseRepository;
import org.fossasia.susi.ai.login.LoginActivity;
import org.fossasia.susi.ai.login.LoginPresenter;
import org.fossasia.susi.ai.login.contract.ILoginView;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import static org.mockito.ArgumentMatchers.any;

@RunWith(JUnit4.class)
public class LoginPresenterTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private ILoginModel loginModel;
    @Mock private ILoginView iLoginView;
    @Mock private UtilModel model;
    @Mock private DatabaseRepository repository;
    @Mock private LoginActivity loginActivity;

    private LoginPresenter loginPresenter;

    private static final String EMAIL = "test";
    private static final String PASSWORD = "test";
    private static final String URL = "test";
    private static final boolean SERVERCONNECTED = true;


    @Before
    public void setUp() {
        loginPresenter = new LoginPresenter(loginActivity);
        loginPresenter.login(EMAIL,PASSWORD,SERVERCONNECTED,URL);
    }

    @Test
    public void shouldShowSuccessOnStart() {
        Mockito.when(model.isLoggedIn()).thenReturn(true);

        loginPresenter.onAttach(iLoginView);

        Mockito.verify(iLoginView).onLoginSuccess((String) any());
    }

    @Test
    public void shouldNotShowSuccessOnStart() {
        Mockito.when(model.isLoggedIn()).thenReturn(false);

        loginPresenter.onAttach(iLoginView);

        Mockito.verify(iLoginView, Mockito.never()).onLoginSuccess((String) any());
    }

}