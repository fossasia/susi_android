package org.fossasia.susi.ai.login;

import org.fossasia.susi.ai.data.UtilModel;
import org.fossasia.susi.ai.login.contract.ILoginView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class LoginPresenterTest {

    @Mock
    private ILoginView mLoginView;

    @Mock
    private LoginActivity mLoginActivity;

    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */

    private LoginPresenter mLoginPresenter;

    @Before
    public void setupLoginPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mLoginPresenter = new LoginPresenter(mLoginActivity);
    }

    @Test
    public void skipLoginIsPressed_OpenChatActivity() {
        // Given an initialized NotesPresenter with initialized notes
        // When loading of Notes is requested
        mLoginPresenter.skipLogin();
        verify(mLoginView).skipLogin();
    }
}
