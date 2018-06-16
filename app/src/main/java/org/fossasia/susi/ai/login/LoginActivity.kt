package org.fossasia.susi.ai.login

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.ChatActivity
import org.fossasia.susi.ai.helper.AlertboxHelper
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.login.contract.ILoginPresenter
import org.fossasia.susi.ai.login.contract.ILoginView
import org.fossasia.susi.ai.signup.SignUpActivity

/**
 * <h1>The Login activity.</h1>
 * <h2>This activity is used to login into the app.</h2>
 *
 * Created by chiragw15 on 4/7/17.
 */
class LoginActivity : AppCompatActivity(), ILoginView {

    lateinit var forgotPasswordProgressDialog: Dialog
    lateinit var builder: AlertDialog.Builder
    private lateinit var loginPresenter: ILoginPresenter
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        PrefManager.putBoolean("activity_executed", true)

        if (savedInstanceState != null) {
            email.editText?.setText(savedInstanceState.getCharSequenceArray(Constant.SAVED_STATES)[0].toString())
            password.editText?.setText(savedInstanceState.getCharSequenceArray(Constant.SAVED_STATES)[1].toString())
            if (savedInstanceState.getBoolean(Constant.SERVER)) {
                inputUrl.visibility = View.VISIBLE
            } else {
                inputUrl.visibility = View.GONE
            }
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.login))

        builder = AlertDialog.Builder(this)
        builder.setView(R.layout.progress)
        forgotPasswordProgressDialog = builder.create()


        addListeners()

        cancelRequestPassword()
        requestPassword()

        loginPresenter = LoginPresenter(this)
        loginPresenter.onAttach(this)
    }

    override fun onLoginSuccess(message: String?) {
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
        val intent = Intent(this@LoginActivity, ChatActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Constant.FIRST_TIME, true)
        startActivity(intent)
        finish()
    }

    override fun skipLogin() {
        val intent = Intent(this@LoginActivity, ChatActivity::class.java)
        intent.putExtra(Constant.FIRST_TIME, false)
        startActivity(intent)
        finish()
    }

    override fun invalidCredentials(isEmpty: Boolean, what: String) {
        if (isEmpty) {
            when (what) {
                Constant.EMAIL -> email.error = getString(R.string.email_cannot_be_empty)
                Constant.PASSWORD -> password.error = getString(R.string.password_cannot_be_empty)
                Constant.INPUT_URL -> inputUrl.error = getString(R.string.url_cannot_be_empty)
            }
        } else {
            when (what) {
                Constant.EMAIL -> email.error = getString(R.string.email_invalid_title)
                Constant.INPUT_URL -> inputUrl.error = getString(R.string.invalid_url)
            }
        }
        logIn.isEnabled = true
        forgotPassword.isEnabled = true
    }

    override fun showProgress(boolean: Boolean) {
        if (boolean) progressDialog.show() else progressDialog.dismiss()
    }

    override fun onLoginError(title: String?, message: String?) {
        val notSuccessAlertboxHelper = AlertboxHelper(this@LoginActivity, title, message, null, null, getString(R.string.ok), null, Color.BLUE)
        notSuccessAlertboxHelper.showAlertBox()
        logIn.isEnabled = true
    }

    override fun attachEmails(savedEmails: MutableSet<String>?) {
        if (savedEmails != null)
            emailInput.setAdapter(ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ArrayList<String>(savedEmails)))
    }

    private fun addListeners() {
        showURL()
        signUp()
        skip()
        logIn()
        cancelLogin()
        onEditorAction()
    }

    private fun showURL() {
        customServer.setOnClickListener { inputUrl.visibility = if (customServer.isChecked) View.VISIBLE else View.GONE }
    }

    private fun signUp() {
        signUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            intent.putExtra("email", email.editText?.text.toString())
            startActivity(intent)
        }
    }

    fun skip() {
        skip.setOnClickListener { loginPresenter.skipLogin() }
    }

    private fun logIn() {
        logIn.setOnClickListener {
            startLogin()
        }
    }

    private fun startLogin() {
        val stringEmail = email.editText?.text.toString()
        val stringPassword = password.editText?.text.toString()
        val stringURL = inputUrl.editText?.text.toString()

        logIn.isEnabled = false
        email.error = null
        password.error = null
        inputUrl.error = null

        loginPresenter.login(stringEmail, stringPassword, !customServer.isChecked, stringURL)
    }

    private fun cancelLogin() {
        progressDialog.setOnCancelListener({
            loginPresenter.cancelLogin()
            logIn.isEnabled = true
        })
    }

    private fun onEditorAction() {
        passwordInput.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_GO) {
                startLogin()
                handled = true
            }
            handled
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val values = arrayOf<CharSequence>(email.editText?.text.toString(), password.editText?.text.toString())
        outState.putCharSequenceArray(Constant.SAVED_STATES, values)
        outState.putBoolean(Constant.SERVER, customServer.isChecked)
    }

    override fun onDestroy() {
        loginPresenter.onDetach()
        super.onDestroy()
    }

    override fun resetPasswordSuccess() {
        startActivity(Intent(this@LoginActivity, ForgotPass::class.java))
    }

    override fun resetPasswordFailure(title: String?, message: String?, button: String?, color: Int) {
        val notSuccessAlertboxHelper = AlertboxHelper(this@LoginActivity, title, message, null, null, button, null, color)
        notSuccessAlertboxHelper.showAlertBox()
    }

    override fun showForgotPasswordProgress(boolean: Boolean) {
        if (boolean) forgotPasswordProgressDialog.show() else forgotPasswordProgressDialog.dismiss()
    }

    fun cancelRequestPassword() {
        progressDialog.setOnCancelListener {
            loginPresenter.cancelSignup()
            forgotPassword.isEnabled = true
        }
    }

    fun requestPassword() {
        forgotPassword.setOnClickListener {
            val email = emailInput?.text.toString()
            val isPersonalServerChecked = customServer.isChecked
            val url = inputUrl.editText?.text.toString()
            emailInput.error = null
            inputUrl.error = null
            forgotPassword.isEnabled = false
            loginPresenter.requestPassword(email, url, isPersonalServerChecked)
        }
    }
}
