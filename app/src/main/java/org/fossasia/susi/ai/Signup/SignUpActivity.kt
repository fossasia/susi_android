package org.fossasia.susi.ai.Signup

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
import org.fossasia.susi.ai.activities.ForgotPasswordActivity
import org.fossasia.susi.ai.login.LoginActivity
import org.fossasia.susi.ai.helper.AlertboxHelper
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.CredentialHelper

/**
 * Created by mayanktripathi on 05/07/17.
 */

class SignUpActivity : AppCompatActivity(), ISignUpView {

    var signUpPresenter: ISignUpPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        addListeners()

        signUpPresenter = SignUpPresenter()
        signUpPresenter?.onAttach(this, applicationContext)

    }

    fun addListeners() {
            showURL()
            hideURL()
            signUp()
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
        val values = arrayOf<CharSequence>(email?.editText!!.text.toString(), password?.editText!!.text.toString(), confirm_password?.editText!!.text.toString())
        outState.putCharSequenceArray(Constant.SAVED_STATES, values)
        outState.putBoolean(Constant.SERVER, personal_server?.isChecked!!)
    }

    override fun onBackPressed() {
        val alertDialog = AlertDialog.Builder(this@SignUpActivity)
        alertDialog.setCancelable(false)
        alertDialog.setMessage(R.string.error_cancelling_signUp_process_text)
        alertDialog.setPositiveButton(applicationContext.getString(R.string.yes)) { dialogInterface, i -> super@SignUpActivity.onBackPressed() }
        alertDialog.setNegativeButton(applicationContext.getString(R.string.no), null)
        val alert = alertDialog.create()
        alert.show()
        val yes = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        val no = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        yes.setTextColor(resources.getColor(R.color.md_blue_500))
        no.setTextColor(resources.getColor(R.color.md_red_500))
    }


    override fun alertSuccess() {
        val dialogClickListener = DialogInterface.OnClickListener { dialogInterface, i -> finish() }
        val alertTitle = resources.getString(R.string.signup)
        val alertMessage = resources.getString(R.string.signup_msg)
        val successAlertboxHelper = AlertboxHelper(this@SignUpActivity, alertTitle, alertMessage, dialogClickListener, null, resources.getString(R.string.ok), null, resources.getColor(R.color.md_blue_500))
        successAlertboxHelper.showAlertBox()    }

    override fun alertFailure() {
        val dialogClickListener = DialogInterface.OnClickListener { dialogInterface, i ->
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        val dialogClickListenern = DialogInterface.OnClickListener { dialogInterface, i ->
            val intent = Intent(this@SignUpActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }
        val alertTitle = resources.getString(R.string.error_email)
        val alertMessage = resources.getString(R.string.error_msg)
        val failureAlertboxHelper = AlertboxHelper(this@SignUpActivity, alertTitle, alertMessage, dialogClickListener, dialogClickListenern, resources.getString(R.string.ok), resources.getString(R.string.forgot_pass_activity), resources.getColor(R.color.md_blue_500))
        failureAlertboxHelper.showAlertBox()
    }

    override fun alertError(title: String, message: String) {
        val errorAlertboxHelper = AlertboxHelper(this@SignUpActivity, title, message, null, null, resources.getString(R.string.ok), null, Color.BLUE)
        errorAlertboxHelper.showAlertBox()
    }

    override fun setErrorEmail(msg: String) {
        email?.setError(msg)
    }

    override fun setErrorPass(msg: String) {
        password?.setError(msg)
    }

    override fun setErrorConpass(msg: String) {
        confirm_password?.setError(msg)
    }

    override fun setErrorUrl(msg: String) {
        input_url?.setError(msg)
    }

    override fun enableSignUp(bool: Boolean) {
        sign_up?.setEnabled(bool)
    }

    override fun isPersonalServer(): Boolean? {
        return personal_server?.isChecked()
    }

    override fun clearField() {
        CredentialHelper.clearFields(email, password, confirm_password)
    }

    override fun checkIfEmptyUrl(): Boolean {
        return CredentialHelper.checkIfEmpty(input_url, this)
    }

    override fun setupPasswordWatcher() {
        password?.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                CredentialHelper.checkPasswordValid(password, this@SignUpActivity)
            }
        }
    }

    override fun getValidURL(): String {
        return CredentialHelper.getValidURL(input_url.editText?.text.toString())
    }

    override fun showProcess(): ProgressDialog {
        val progressDialog = ProgressDialog(this@SignUpActivity)
        progressDialog.setCancelable(false)
        progressDialog.setMessage(applicationContext.getString(R.string.signing_up))
        progressDialog.show()
        return progressDialog
    }

    override fun checkCredentials(): Boolean {
        return CredentialHelper.checkIfEmpty(email, this) or
                CredentialHelper.checkIfEmpty(password, this) or
                CredentialHelper.checkIfEmpty(confirm_password, this)
    }

    override fun isEmailValid(email: String): Boolean {
        return CredentialHelper.isEmailValid(email)
    }

    override fun checkPasswordValid(): Boolean {
        return CredentialHelper.checkPasswordValid(password, this);
    }

    override fun isURLValid(): Boolean {
        return CredentialHelper.isURLValid(input_url,this);
    }

    override fun clearFiled() {
        CredentialHelper.clearFields(email, password, confirm_password)
    }

    fun showURL() {
        personal_server.setOnClickListener {
                input_url?.visibility = View.VISIBLE
        }
    }

    fun hideURL() {
        susi_default.setOnClickListener {
            input_url?.visibility = View.GONE
        }
    }

    fun signUp() {

        sign_up.setOnClickListener {

            val stringEmail = email.editText?.text.toString()
            val stringPassword = password.editText?.text.toString()
            val stringConPassword = confirm_password.editText?.text.toString()

            signUpPresenter?.signUp(stringEmail, stringPassword, stringConPassword)
        }
    }

}
