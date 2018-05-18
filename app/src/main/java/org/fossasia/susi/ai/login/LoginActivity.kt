package org.fossasia.susi.ai.login

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.signup.SignUpActivity
import org.fossasia.susi.ai.chat.ChatActivity
import org.fossasia.susi.ai.forgotpassword.ForgotPasswordActivity
import org.fossasia.susi.ai.helper.AlertboxHelper
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.login.contract.ILoginPresenter
import org.fossasia.susi.ai.login.contract.ILoginView

/**
 * <h1>The Login activity.</h1>
 * <h2>This activity is used to login into the app.</h2>
 *
 * Created by chiragw15 on 4/7/17.
 */
class LoginActivity : AppCompatActivity(), ILoginView {

    lateinit var loginPresenter: ILoginPresenter
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (savedInstanceState != null) {
            email.editText?.setText(savedInstanceState.getCharSequenceArray(Constant.SAVED_STATES)[0].toString())
            password.editText?.setText(savedInstanceState.getCharSequenceArray(Constant.SAVED_STATES)[1].toString())
            if (savedInstanceState.getBoolean(Constant.SERVER)) {
                input_url.visibility = View.VISIBLE
            } else {
                input_url.visibility = View.GONE
            }
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.login))

        addListeners()
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
        if(isEmpty) {
            when(what) {
                Constant.EMAIL -> email.error = getString(R.string.email_cannot_be_empty)
                Constant.PASSWORD -> password.error = getString(R.string.password_cannot_be_empty)
                Constant.INPUT_URL -> input_url.error = getString(R.string.url_cannot_be_empty)
            }
        } else {
            when(what) {
                Constant.EMAIL -> email.error = getString(R.string.email_invalid_title)
                Constant.INPUT_URL -> input_url.error = getString(R.string.invalid_url)
            }
        }
        log_in.isEnabled = true
    }

    override fun showProgress(boolean: Boolean) {
        if (boolean) progressDialog.show() else progressDialog.dismiss()
    }

    override fun onLoginError(title: String?, message: String?) {
        val notSuccessAlertboxHelper = AlertboxHelper(this@LoginActivity, title, message, null, null, getString(R.string.ok), null, Color.BLUE)
        notSuccessAlertboxHelper.showAlertBox()
        log_in.isEnabled = true
    }

    override fun attachEmails(savedEmails: MutableSet<String>?) {
        if (savedEmails != null)
            email_input.setAdapter(ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ArrayList<String>(savedEmails)))
    }

    fun addListeners() {
        showURL()
        signUp()
        forgotPassword()
        skip()
        logIn()
        cancelLogin()
        onEditorAction()
    }

    fun showURL() {
        customer_server.setOnClickListener { input_url.visibility = if(customer_server.isChecked) View.VISIBLE else View.GONE}
    }

    fun signUp() {
        sign_up.setOnClickListener { startActivity(Intent(this@LoginActivity, SignUpActivity::class.java)) }
    }

    fun forgotPassword() {
        forgot_password.setOnClickListener { startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java)) }
    }

    fun skip() {
        skip.setOnClickListener { loginPresenter.skipLogin() }
    }

    fun logIn() {
        log_in.setOnClickListener {
            startLogin()
        }
    }

    fun startLogin() {
        val stringEmail = email.editText?.text.toString()
        val stringPassword = password.editText?.text.toString()
        val stringURL = input_url.editText?.text.toString()

        log_in.isEnabled = false
        email.error = null
        password.error = null
        input_url.error = null

        loginPresenter.login(stringEmail, stringPassword, !customer_server.isChecked, stringURL)
    }

    fun cancelLogin() {
        progressDialog.setOnCancelListener({
            loginPresenter.cancelLogin()
            log_in.isEnabled = true
        })
    }

    fun onEditorAction(){
        password_input.setOnEditorActionListener { _, actionId, _ ->
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
        outState.putBoolean(Constant.SERVER, customer_server.isChecked)
    }

    override fun onDestroy() {
        loginPresenter.onDetach()
        super.onDestroy()
    }
}
