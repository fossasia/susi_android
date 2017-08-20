package org.fossasia.susi.ai.signup

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.forgotpassword.ForgotPasswordActivity
import org.fossasia.susi.ai.login.LoginActivity
import org.fossasia.susi.ai.helper.AlertboxHelper
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.CredentialHelper
import org.fossasia.susi.ai.signup.contract.ISignUpPresenter
import org.fossasia.susi.ai.signup.contract.ISignUpView

/**
 * <h1>The SignUp activity.</h1>
 * <h2>This activity is used to signUp into the app.</h2>
 *
 * Created by mayanktripathi on 05/07/17.
 */

class SignUpActivity : AppCompatActivity(), ISignUpView {

    lateinit var signUpPresenter: ISignUpPresenter
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setupPasswordWatcher()

        if(savedInstanceState!=null){
            email.editText?.setText(savedInstanceState.getCharSequenceArray(Constant.SAVED_STATES)[0].toString())
            password.editText?.setText(savedInstanceState.getCharSequenceArray(Constant.SAVED_STATES)[1].toString())
            confirm_password.editText?.setText(savedInstanceState.getCharSequenceArray(Constant.SAVED_STATES)[2].toString())
            if(savedInstanceState.getBoolean(Constant.SERVER)) {
                input_url.visibility = View.VISIBLE
            } else {
                input_url.visibility = View.GONE
            }
        }

        progressDialog = ProgressDialog(this@SignUpActivity)
        progressDialog.setCancelable(false)
        progressDialog.setMessage(this.getString(R.string.signing_up))

        addListeners()
        signUpPresenter = SignUpPresenter(this)
        signUpPresenter.onAttach(this)

    }

    fun addListeners() {
        showURL()
        signUp()
        cancelSignUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val values = arrayOf<CharSequence>(email.editText?.text.toString(), password.editText?.text.toString(), confirm_password.editText?.text.toString())
        outState.putCharSequenceArray(Constant.SAVED_STATES, values)
        outState.putBoolean(Constant.SERVER, customer_server.isChecked)
    }

    override fun onBackPressed() {
        val alertDialog = AlertDialog.Builder(this@SignUpActivity)
        alertDialog.setCancelable(false)
        alertDialog.setMessage(R.string.error_cancelling_signUp_process_text)
        alertDialog.setPositiveButton(R.string.yes) { _, _ -> super@SignUpActivity.onBackPressed() }
        alertDialog.setNegativeButton((R.string.no), null)
        val alert = alertDialog.create()
        alert.show()
    }

    override fun alertSuccess() {
        val dialogClickListener = DialogInterface.OnClickListener { _, _ -> finish() }
        val alertTitle = getString(R.string.signup)
        val alertMessage = getString(R.string.signup_msg)
        val successAlertboxHelper = AlertboxHelper(this@SignUpActivity, alertTitle, alertMessage, dialogClickListener, null, resources.getString(R.string.ok), null, resources.getColor(R.color.md_blue_500))
        successAlertboxHelper.showAlertBox()
    }

    override fun alertFailure() {
        val dialogClickListener = DialogInterface.OnClickListener { _, _ ->
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        val dialogClickListenern = DialogInterface.OnClickListener { _, _ ->
            val intent = Intent(this@SignUpActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }
        val alertTitle = getString(R.string.error_email)
        val alertMessage = getString(R.string.error_msg)
        val failureAlertboxHelper = AlertboxHelper(this@SignUpActivity, alertTitle, alertMessage, dialogClickListener, dialogClickListenern, resources.getString(R.string.ok), resources.getString(R.string.forgot_pass_activity), resources.getColor(R.color.md_blue_500))
        failureAlertboxHelper.showAlertBox()
    }

    override fun setErrorConpass(msg: String) {
        confirm_password?.error = msg
    }

    override fun enableSignUp(bool: Boolean) {
        sign_up?.isEnabled = bool
    }

    override fun clearField() {
        CredentialHelper.clearFields(email, password, confirm_password)
    }

    override fun showProgress(bool: Boolean) {
        if (bool) progressDialog.show() else progressDialog.dismiss()
    }

    override fun invalidCredentials(isEmpty: Boolean, what: String) {
        if (isEmpty) {
            when (what) {
                Constant.EMAIL -> email.error = getString(R.string.email_cannot_be_empty)
                Constant.PASSWORD -> password.error = getString(R.string.password_cannot_be_empty)
                Constant.INPUT_URL -> input_url.error = getString(R.string.url_cannot_be_empty)
                Constant.CONFIRM_PASSWORD -> confirm_password.error = getString(R.string.field_cannot_be_empty)
            }
        } else {
            when (what) {
                Constant.EMAIL -> email.error = getString(R.string.invalid_email)
                Constant.INPUT_URL -> input_url.error = getString(R.string.invalid_url)
                Constant.PASSWORD -> password.error = getString(R.string.error_password_matching)
            }
        }
    }

    override fun passwordInvalid() {
        password.error = getString(R.string.pass_validation_text)
        sign_up.isEnabled = true
    }

    fun showURL() {
        customer_server.setOnClickListener { input_url.visibility = if(customer_server.isChecked) View.VISIBLE else View.GONE}
    }

    fun setupPasswordWatcher() {
        password.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            password.error = null
            if (!hasFocus)
                signUpPresenter.checkForPassword(password.editText?.text.toString())
        }
    }

    fun cancelSignUp() {
        progressDialog.setOnCancelListener({
            signUpPresenter.cancelSignUp()
            sign_up.isEnabled = true
        })
    }

    override fun onSignUpError(title: String?, message: String?) {
        val notSuccessAlertboxHelper = AlertboxHelper(this@SignUpActivity, title, message, null, null, getString(R.string.ok), null, Color.BLUE)
        notSuccessAlertboxHelper.showAlertBox()
        sign_up.isEnabled = true
    }

    fun signUp() {

        sign_up.setOnClickListener {

            email.error = null
            password.error = null
            confirm_password.error = null
            input_url.error = null

            val stringEmail = email.editText?.text.toString()
            val stringPassword = password.editText?.text.toString()
            val stringConPassword = confirm_password.editText?.text.toString()
            val stringURL = input_url.editText?.text.toString()

            signUpPresenter.signUp(stringEmail, stringPassword, stringConPassword, !customer_server.isChecked, stringURL)
        }
    }

    override fun onDestroy() {
        signUpPresenter.onDetach()
        super.onDestroy()
    }
}
