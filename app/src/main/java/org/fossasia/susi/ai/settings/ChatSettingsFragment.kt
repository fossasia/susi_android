package org.fossasia.susi.ai.settings

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import android.widget.Toast
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.login.LoginActivity
import org.fossasia.susi.ai.settings.contract.ISettingsPresenter
import org.fossasia.susi.ai.settings.contract.ISettingsView

/**
 * The Fragment for Settings Activity
 *
 * Created by mayanktripathi on 10/07/17.
 */

class ChatSettingsFragment : PreferenceFragmentCompat(), ISettingsView {

    lateinit var settingsPresenter: ISettingsPresenter

    lateinit var textToSpeech: Preference
    lateinit var rate: Preference
    lateinit var server: Preference
    lateinit var micSettings: Preference
    lateinit var hotwordSettings: Preference
    lateinit var share: Preference
    lateinit var loginLogout: Preference
    lateinit var resetPassword: Preference
    var resetPasswordView: View?= null
    var password: TextInputLayout?= null
    var newPassword: TextInputLayout?= null
    var conPassword: TextInputLayout?= null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_settings)

        settingsPresenter = SettingsPresenter(activity as SettingsActivity)
        settingsPresenter.onAttach(this)

        textToSpeech = preferenceManager.findPreference(Constant.LANG_SELECT)
        rate = preferenceManager.findPreference(Constant.RATE)
        server = preferenceManager.findPreference(Constant.SELECT_SERVER)
        micSettings = preferenceManager.findPreference(Constant.MIC_INPUT)
        hotwordSettings = preferenceManager.findPreference(Constant.HOTWORD_DETECTION)
        share = preferenceManager.findPreference(Constant.SHARE)
        loginLogout = preferenceManager.findPreference(Constant.LOGIN_LOGOUT)
        resetPassword = preferenceManager.findPreference(Constant.RESET_PASSWORD)

        if(settingsPresenter.getAnonymity()) {
            loginLogout.title = "Login"
        } else {
            loginLogout.title = "Logout"
        }

        textToSpeech.setOnPreferenceClickListener {
            val intent = Intent()
            intent.component = ComponentName("com.android.settings", "com.android.settings.Settings\$TextToSpeechSettingsActivity")
            startActivity(intent)
            true
        }

        rate.setOnPreferenceClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)))
            true
        }

        share.setOnPreferenceClickListener {
            try {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        String.format(getString(R.string.promo_msg_template),
                                String.format(getString(R.string.app_share_url), activity.packageName)))
                startActivity(shareIntent)
            } catch (e: Exception) {
                showToast(getString(R.string.error_msg_retry))
            }
            true
        }

        loginLogout.setOnPreferenceClickListener {
            if (!settingsPresenter.getAnonymity()) {
                val d = AlertDialog.Builder(activity)
                d.setMessage("Are you sure ?").setCancelable(false).setPositiveButton("Yes") { _, _ ->
                    settingsPresenter.loginLogout()
                }.setNegativeButton("No") { dialog, _ -> dialog.cancel() }

                val alert = d.create()
                alert.setTitle(getString(R.string.logout))
                alert.show()
            } else {
                settingsPresenter.loginLogout()
            }
            true
        }

        resetPassword.setOnPreferenceClickListener {
            val builder = AlertDialog.Builder(activity)
            resetPasswordView = activity.layoutInflater.inflate(R.layout.alert_reset_password, null)
            password = resetPasswordView?.findViewById(R.id.password) as TextInputLayout
            newPassword = resetPasswordView?.findViewById(R.id.newpassword) as TextInputLayout
            conPassword = resetPasswordView?.findViewById(R.id.confirmpassword) as TextInputLayout
            builder.setView(resetPasswordView)
            builder.setTitle("")
                    .setCancelable(false)
                    .setNegativeButton(Constant.CANCEL, null)
                    .setPositiveButton(activity.getString(R.string.ok), {dialog, _ ->
                        settingsPresenter.resetPassword(password?.editText?.text.toString(), newPassword?.editText?.text.toString(), conPassword?.editText?.text.toString())
                    })
            true
        }

        if(settingsPresenter.getAnonymity()){
            server.isEnabled = true
            server.setOnPreferenceClickListener {
                showAlert()
                true
            }
        }
        else {
            server.isEnabled = false
        }

        micSettings.isEnabled = settingsPresenter.enableMic()

        hotwordSettings.isEnabled = settingsPresenter.enableHotword()
    }

    fun showAlert() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(Constant.CHANGE_SERVER)
        builder.setMessage(Constant.SERVER_CHANGE_PROMPT)
                .setCancelable(false)
                .setNegativeButton(Constant.CANCEL, null)
                .setPositiveButton(activity.getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                    settingsPresenter.loginLogout()

                }
        val alert = builder.create()
        alert.show()
    }

    override fun micPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    override fun hotWordPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun startLoginActivity() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        activity.finish()
    }

    override fun passwordInvalid(what: String) {
        invalidPassword(what)
    }

    override fun invalidCredentials(isEmpty: Boolean, what: String) {
        credentialInvalid(isEmpty, what)
    }

    fun invalidPassword(what: String) {
        when(what) {
            Constant.PASSWORD -> password?.error =  getString(R.string.pass_validation_text)
            Constant.NEWPASSWORD -> newPassword?.error =  getString(R.string.pass_validation_text)
        }
    }

    fun credentialInvalid(isEmpty: Boolean, what: String) {
        if(isEmpty) {
            when(what) {
                Constant.PASSWORD -> password?.error = getString(R.string.email_cannot_be_empty)
                Constant.NEWPASSWORD -> newPassword?.error = getString(R.string.email_cannot_be_empty)
                Constant.CONFIRM_PASSWORD -> conPassword?.error = getString(R.string.email_cannot_be_empty)
            }
        } else {
            newPassword?.error = getString(R.string.error_password_matching)
        }
    }

    fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        settingsPresenter.onDetach()
    }
}
