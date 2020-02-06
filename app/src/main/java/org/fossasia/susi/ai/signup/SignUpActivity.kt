package org.fossasia.susi.ai.signup

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.ChatActivity
import org.fossasia.susi.ai.helper.AlertboxHelper
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.CredentialHelper
import org.fossasia.susi.ai.login.ForgotPass
import org.fossasia.susi.ai.login.LoginActivity
import org.fossasia.susi.ai.signup.contract.ISignUpPresenter
import org.fossasia.susi.ai.signup.contract.ISignUpView
import org.fossasia.susi.ai.skills.SkillsActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

/**
 * <h1>The SignUp activity.</h1>
 * <h2>This activity is used to signUp into the app.</h2>
 *
 * Created by mayanktripathi on 05/07/17.
 */

class SignUpActivity : AppCompatActivity(), ISignUpView {

    private val signUpPresenter: ISignUpPresenter by inject { parametersOf(this) }
    private lateinit var progressDialog: ProgressDialog
    private lateinit var forgotPasswordProgressDialog: Dialog
    private lateinit var builder: AlertDialog.Builder
    private var checkDialog: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setupPasswordWatcher()

        if (savedInstanceState != null) {
            email.editText?.setText(savedInstanceState.getCharSequenceArray(Constant.SAVED_STATES)[0].toString())
            password.editText?.setText(savedInstanceState.getCharSequenceArray(Constant.SAVED_STATES)[1].toString())
            confirmPassword.editText?.setText(savedInstanceState.getCharSequenceArray(Constant.SAVED_STATES)[2].toString())

            if (savedInstanceState.getBoolean(Constant.SERVER)) {
                inputUrlSignUp.visibility = View.VISIBLE
            } else {
                inputUrlSignUp.visibility = View.GONE
            }

            if (savedInstanceState.getBoolean(Constant.SAVE_DIALOG_STATE)) {
                onBackPressed()
            }
        }

        val bundle = intent.extras
        val string = bundle?.getString("email")
        if (string != null)
            email.editText?.setText(string)

        progressDialog = ProgressDialog(this@SignUpActivity)
        progressDialog.setCancelable(false)
        progressDialog.setMessage(this.getString(R.string.signing_up))

        builder = AlertDialog.Builder(this)
        builder.setView(R.layout.progress)
        forgotPasswordProgressDialog = builder.create()

        addListeners()

        cancelRequestPassword()
    }

    private fun addListeners() {
        showURL()
        signUp()
        signUpToLoginPage()
        cancelSignUp()
        signUpToTermsConditionPage()
        skipSignUp()
    }

    fun skipSignUp() {
        skipSignUp.setOnClickListener {
            val dialogClickListener = AlertDialog.Builder(this)
            with(dialogClickListener) {
                setTitle(R.string.dialog_skip_sign_up)
                setMessage(R.string.error_skipping_signUp_process_text)
                setPositiveButton(R.string.yes, DialogInterface.OnClickListener { dialog, id ->
                    val intent = Intent(this@SignUpActivity, ChatActivity::class.java)
                    startActivity(intent)
                    finish()
                })
                setNegativeButton(android.R.string.no, DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
                })
                show()
            }
        }
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
        val values = arrayOf<CharSequence>(email.editText?.text.toString(), password.editText?.text.toString(), confirmPassword.editText?.text.toString())
        outState.putCharSequenceArray(Constant.SAVED_STATES, values)
        outState.putBoolean(Constant.SERVER, customServerSignUp.isChecked)
        outState.putBoolean(Constant.SAVE_DIALOG_STATE, checkDialog)
    }

    override fun onBackPressed() {
        val dialogClickListener = DialogInterface.OnClickListener { _, _ -> super@SignUpActivity.onBackPressed() }
        val alertMessage = getString(R.string.error_cancelling_signUp_process_text)
        val dialogTitle = getString(R.string.dialog_cancel_sign_up)
        val successAlertboxHelper = AlertboxHelper(this@SignUpActivity, dialogTitle, alertMessage, dialogClickListener, null,
                resources.getString(R.string.cancel), resources.getString(R.string.stay_here), resources.getColor(R.color
                .md_blue_500))
        successAlertboxHelper.showAlertBox()
        checkDialog = true
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
        val dialogClickListener1 = DialogInterface.OnClickListener { _, _ ->
            val email1 = email.editText?.text.toString()
            val isPersonalServerChecked = customServerSignUp.isChecked
            val url = inputUrlSignUp.editText?.text.toString()
            email.error = null
            inputUrlSignUp.error = null
            signUpPresenter.requestPassword(email1, url, isPersonalServerChecked)
        }
        val alertTitle = getString(R.string.error_email)
        val alertMessage = getString(R.string.error_msg)
        val failureAlertboxHelper = AlertboxHelper(this@SignUpActivity, alertTitle, alertMessage, dialogClickListener, dialogClickListener1, resources.getString(R.string.ok), resources.getString(R.string.forgot_pass_activity), resources.getColor(R.color.md_blue_500))
        failureAlertboxHelper.showAlertBox()
    }

    override fun setErrorConpass(msg: String) {
        confirmPassword?.error = msg
    }

    override fun enableSignUp(bool: Boolean) {
        signUp?.isEnabled = bool
    }

    override fun clearField() {
        CredentialHelper.clearFields(email, password, confirmPassword)
    }

    override fun showProgress(bool: Boolean) {
        if (bool) progressDialog.show() else progressDialog.dismiss()
    }

    override fun invalidCredentials(isEmpty: Boolean, what: String) {
        if (isEmpty) {
            when (what) {
                Constant.EMAIL -> email.error = getString(R.string.email_cannot_be_empty)
                Constant.PASSWORD -> password.error = getString(R.string.password_cannot_be_empty)
                Constant.INPUT_URL -> inputUrlSignUp.error = getString(R.string.url_cannot_be_empty)
                Constant.CONFIRM_PASSWORD -> confirmPassword.error = getString(R.string.field_cannot_be_empty)
                Constant.ACCEPT_TERMS_AND_CONDITIONS -> {
                    acceptTermsAndConditions.error = getString(R.string.accept_terms_and_conditions)
                    Toast.makeText(this@SignUpActivity, R.string.accept_terms_and_conditions, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            when (what) {
                Constant.EMAIL -> email.error = getString(R.string.invalid_email)
                Constant.INPUT_URL -> inputUrlSignUp.error = getString(R.string.invalid_url)
                Constant.PASSWORD -> password.error = getString(R.string.error_password_matching)
            }
        }
    }

    override fun passwordInvalid() {
        password.error = getString(R.string.pass_validation_text)
        signUp.isEnabled = true
    }

    private fun showURL() {
        customServerSignUp.setOnClickListener { inputUrlSignUp.visibility = if (customServerSignUp.isChecked) View.VISIBLE else View.GONE }
    }

    private fun setupPasswordWatcher() {
        password.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            password.error = null
            if (!hasFocus)
                signUpPresenter.checkForPassword(password.editText?.text.toString())
        }
    }

    private fun signUpToLoginPage() {
        signUpToLogin.setOnClickListener {
            signUpPresenter.loginLogout()
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            intent.putExtra("email", email.editText?.text.toString())
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun signUpToTermsConditionPage() {
        signUpToTermsCondition.setOnClickListener {
            val intent = Intent(this@SignUpActivity, SkillsActivity::class.java)
            intent.putExtra(Constant.SIGN_UP_TO_PRIVACY, true)
            startActivity(intent)
        }
    }

    private fun cancelSignUp() {
        progressDialog.setOnCancelListener {
            signUpPresenter.cancelSignUp()
            signUp.isEnabled = true
        }
    }

    override fun onSignUpError(title: String?, message: String?) {
        val notSuccessAlertboxHelper = AlertboxHelper(this@SignUpActivity, title, message, null, null, getString(R.string.ok), null, Color.BLUE)
        notSuccessAlertboxHelper.showAlertBox()
        signUp.isEnabled = true
    }

    private fun signUp() {

        signUp.setOnClickListener {

            email.error = null
            password.error = null
            confirmPassword.error = null
            inputUrlSignUp.error = null

            val stringEmail = email.editText?.text.toString()
            val stringPassword = password.editText?.text.toString()
            val stringConfirmPassword = confirmPassword.editText?.text.toString()
            val stringURL = inputUrlSignUp.editText?.text.toString()

            signUpPresenter.signUp(stringEmail, stringPassword, stringConfirmPassword, !customServerSignUp.isChecked, stringURL, acceptTermsAndConditions.isChecked)
        }
    }

    override fun showForgotPasswordProgress(boolean: Boolean) {
        if (boolean) forgotPasswordProgressDialog.show() else forgotPasswordProgressDialog.dismiss()
    }

    override fun resetPasswordSuccess() {
        startActivity(Intent(this@SignUpActivity, ForgotPass::class.java))
    }

    override fun resetPasswordFailure(title: String?, message: String?, button: String?, color: Int) {
        val notSuccessAlertboxHelper = AlertboxHelper(this@SignUpActivity, title, message, null, null, button, null, color)
        notSuccessAlertboxHelper.showAlertBox()
    }

    private fun cancelRequestPassword() {
        progressDialog.setOnCancelListener {
            signUpPresenter.cancelSignup()
        }
    }
}
