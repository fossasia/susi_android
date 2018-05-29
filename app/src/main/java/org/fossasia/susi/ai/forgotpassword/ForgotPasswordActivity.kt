package org.fossasia.susi.ai.forgotpassword

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.forgotpassword.contract.IForgotPasswordPresenter
import org.fossasia.susi.ai.forgotpassword.contract.IForgotPasswordView
import org.fossasia.susi.ai.helper.AlertboxHelper
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.login.LoginActivity

/**
 * <h1>The ForgotPassword activity.</h1>
 * <h2>This activity is used to request password if user forgot password.</h2>
 *
 * Created by meeera on 6/7/17.
 */
class ForgotPasswordActivity : AppCompatActivity(), IForgotPasswordView {

    lateinit var forgotPasswordPresenter: IForgotPasswordPresenter
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        title = getString(R.string.forgot_pass_activity)

        if (savedInstanceState != null) {
            forgot_email.editText?.setText(savedInstanceState.getCharSequenceArray(Constant.SAVED_STATES)[0].toString())
            if (savedInstanceState.getBoolean(Constant.SERVER)) {
                input_url.visibility = View.VISIBLE
            } else {
                input_url.visibility = View.GONE
            }
        }

        val bundle = intent.extras;
        val string = bundle?.getString("email")
        if (string != null)
            forgot_email.editText?.setText(string)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.reset_password_message))

        showUrl()
        cancelRequestPassword()
        requestPassword()

        forgotPasswordPresenter = ForgotPasswordPresenter(this)
        forgotPasswordPresenter.onAttach(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun success(title: String?, message: String?) {
        val dialogClickListener = DialogInterface.OnClickListener { _, _ ->
            val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        val successAlertboxHelper = AlertboxHelper(this@ForgotPasswordActivity, title, message, dialogClickListener, null, resources.getString(R.string.Continue), null, Color.BLUE)
        successAlertboxHelper.showAlertBox()
        reset_button.isEnabled = true
    }

    override fun failure(title: String?, message: String?, button: String?, color: Int) {
        val notSuccessAlertboxHelper = AlertboxHelper(this@ForgotPasswordActivity, title, message, null, null, button, null, color)
        notSuccessAlertboxHelper.showAlertBox()
        reset_button.isEnabled = true
    }

    fun showUrl() {
        custom_server.setOnClickListener {
            input_url.visibility = if (custom_server.isChecked) View.VISIBLE else View.GONE
        }
    }

    fun cancelRequestPassword() {
        progressDialog.setOnCancelListener {
            forgotPasswordPresenter.cancelSignup()
            reset_button.isEnabled = true
        }
    }

    fun requestPassword() {
        reset_button.setOnClickListener {
            val email = forgot_email.editText?.text.toString()
            val isPersonalServerChecked = custom_server.isChecked
            val url = input_url.editText?.text.toString()
            forgot_email.error = null
            input_url.error = null
            reset_button.isEnabled = false
            forgotPasswordPresenter.requestPassword(email, url, isPersonalServerChecked)
        }
    }

    override fun invalidCredentials(isEmpty: Boolean, what: String) {
        if (isEmpty) {
            when (what) {
                Constant.EMAIL -> forgot_email.error = getString(R.string.email_cannot_be_empty)
                Constant.INPUT_URL -> input_url.error = getString(R.string.url_cannot_be_empty)
            }
        } else {
            when (what) {
                Constant.EMAIL -> forgot_email.error = getString(R.string.email_invalid_title)
                Constant.INPUT_URL -> input_url.error = getString(R.string.invalid_url)
            }
        }
        reset_button.isEnabled = true
    }

    override fun showProgress(boolean: Boolean) {
        if (boolean) progressDialog.show() else progressDialog.hide()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val values = arrayOf<CharSequence>(forgot_email.editText?.text.toString())
        outState.putCharSequenceArray(Constant.SAVED_STATES, values)
        outState.putBoolean(Constant.SERVER, custom_server.isChecked)
    }

    override fun onDestroy() {
        super.onDestroy()
        forgotPasswordPresenter.onDetach()
    }
}