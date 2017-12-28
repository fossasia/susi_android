package org.fossasia.susi.ai.skills.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.widget.AppCompatCheckBox
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.login.LoginActivity
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.skills.settings.contract.ISettingsPresenter
import org.fossasia.susi.ai.skills.settings.contract.ISettingsView
import org.fossasia.susi.ai.skills.SkillsActivity

/**
 * The Fragment for Settings Activity
 *
 * Created by mayanktripathi on 10/07/17.
 */

class ChatSettingsFragment : PreferenceFragmentCompat(), ISettingsView {

    lateinit var settingsPresenter: ISettingsPresenter

    lateinit var rate: Preference
    lateinit var server: Preference
    lateinit var micSettings: Preference
    lateinit var hotwordSettings: Preference
    lateinit var share: Preference
    lateinit var loginLogout: Preference
    lateinit var resetPassword: Preference
    lateinit var enterSend: Preference
    lateinit var speechAlways: Preference
    lateinit var speechOutput: Preference
    lateinit var displayEmail: Preference
    lateinit var password: TextInputLayout
    lateinit var newPassword: TextInputLayout
    lateinit var conPassword: TextInputLayout
    lateinit var input_url: TextInputLayout
    lateinit var resetPasswordAlert: AlertDialog
    lateinit var setServerAlert: AlertDialog
    lateinit var querylanguage: ListPreference
    var flag = true


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_settings)

        (activity as SkillsActivity).title = (activity as SkillsActivity).getString(R.string.action_settings)
        settingsPresenter = SettingsPresenter(activity as SkillsActivity)
        settingsPresenter.onAttach(this)

        setHasOptionsMenu(true)

        rate = preferenceManager.findPreference(Constant.RATE)
        server = preferenceManager.findPreference(Constant.SELECT_SERVER)
        micSettings = preferenceManager.findPreference(Constant.MIC_INPUT)
        hotwordSettings = preferenceManager.findPreference(Constant.HOTWORD_DETECTION)
        share = preferenceManager.findPreference(Constant.SHARE)
        loginLogout = preferenceManager.findPreference(Constant.LOGIN_LOGOUT)
        resetPassword = preferenceManager.findPreference(Constant.RESET_PASSWORD)
        enterSend = preferenceManager.findPreference(Constant.ENTER_SEND)
        speechOutput = preferenceManager.findPreference(Constant.SPEECH_OUTPUT)
        speechAlways = preferenceManager.findPreference(Constant.SPEECH_ALWAYS)
        displayEmail = preferenceManager.findPreference("display_email")
        querylanguage = preferenceManager.findPreference(Constant.LANG_SELECT) as ListPreference

        // Display login email
        var utilModel: UtilModel = UtilModel(activity as SkillsActivity)
        if (utilModel.isLoggedIn() == false)
            displayEmail.title = "Not logged in"
        else
            displayEmail.title = PrefManager.getStringSet(Constant.SAVED_EMAIL).iterator().next().toString()

        setLanguage()
        if (settingsPresenter.getAnonymity()) {
            loginLogout.title = "Login"
        } else {
            loginLogout.title = "Logout"
        }

        querylanguage.setOnPreferenceChangeListener { _, newValue ->
            PrefManager.putString(Constant.LANGUAGE, newValue.toString())
            setLanguage()
            if(!settingsPresenter.getAnonymity()) {
                settingsPresenter.sendSetting(Constant.LANGUAGE, newValue.toString(), 1)
            }
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

        if (settingsPresenter.getAnonymity()) {
            server.isEnabled = true
            server.setOnPreferenceClickListener {
                showAlert()
                true
            }
        } else {
            server.isEnabled = false
        }

        if(!settingsPresenter.getAnonymity()) {
            resetPassword.isEnabled = true
            resetPassword.setOnPreferenceClickListener {
                showResetPasswordAlert()
                true
            }
        } else {
            resetPassword.isEnabled = false
        }

        micSettings.isEnabled = settingsPresenter.enableMic()

        hotwordSettings.isEnabled = settingsPresenter.enableHotword()

        if(!settingsPresenter.getAnonymity()) {
            micSettings.setOnPreferenceClickListener {
                settingsPresenter.sendSetting(Constant.MIC_INPUT, (PrefManager.getBoolean(Constant.MIC_INPUT, false)).toString(), 1)
                true
            }

            enterSend.setOnPreferenceChangeListener { _, newValue ->
                settingsPresenter.sendSetting(Constant.ENTER_SEND, newValue.toString(), 1)
                true
            }

            speechAlways.setOnPreferenceChangeListener { _, newValue ->
                settingsPresenter.sendSetting(Constant.SPEECH_ALWAYS, newValue.toString(), 1)
                true
            }

            speechOutput.setOnPreferenceChangeListener { _, newValue ->
                settingsPresenter.sendSetting(Constant.SPEECH_OUTPUT, newValue.toString(), 1)
                true
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val itemSettings = menu.findItem(R.id.menu_settings)
        itemSettings.isVisible = false
        val itemAbout = menu.findItem(R.id.menu_about)
        itemAbout.isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    fun setLanguage() {
        val index = querylanguage.findIndexOfValue(PrefManager.getString(Constant.LANGUAGE, Constant.DEFAULT))
        querylanguage.setValueIndex(index)
        querylanguage.summary = querylanguage.entries[index]
    }

    fun showAlert() {
        val builder = AlertDialog.Builder(activity)
        val promptsView = activity.layoutInflater.inflate(R.layout.alert_change_server, null)
        input_url = promptsView.findViewById(R.id.input_url) as TextInputLayout
        val input_url_text = promptsView.findViewById(R.id.input_url_text) as TextInputEditText
        val customer_server = promptsView.findViewById(R.id.customer_server) as AppCompatCheckBox
        if (PrefManager.getBoolean(Constant.SUSI_SERVER, true)) {
            input_url.visibility = View.GONE
            flag = false
        } else {
            input_url.visibility = View.VISIBLE
            flag = true
        }
        customer_server.isChecked = flag
        input_url_text.setText(PrefManager.getString(Constant.CUSTOM_SERVER, null))
        customer_server.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
                input_url.visibility = View.VISIBLE
            if(!isChecked)
                input_url.visibility = View.GONE
        }
        builder.setView(promptsView)
        builder.setTitle(Constant.CHANGE_SERVER)
                .setCancelable(false)
                .setNegativeButton(Constant.CANCEL, null)
                .setPositiveButton(activity.getString(R.string.ok), null)
        setServerAlert = builder.create()
        setServerAlert.show()
        setServerAlert.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            settingsPresenter.setServer(customer_server.isChecked, input_url.editText?.text.toString())
        }
    }

    fun showResetPasswordAlert() {
        val builder = AlertDialog.Builder(activity)
        val resetPasswordView = activity.layoutInflater.inflate(R.layout.alert_reset_password, null)
        password = resetPasswordView.findViewById(R.id.password) as TextInputLayout
        newPassword = resetPasswordView.findViewById(R.id.newpassword) as TextInputLayout
        conPassword = resetPasswordView.findViewById(R.id.confirmpassword) as TextInputLayout
        builder.setView(resetPasswordView)
        builder.setTitle(Constant.CHANGE_PASSWORD)
                .setCancelable(false)
                .setNegativeButton(Constant.CANCEL, null)
                .setPositiveButton(getString(R.string.ok), null)
        resetPasswordAlert = builder.create()
        resetPasswordAlert.show()
        setupPasswordWatcher()
        resetPasswordAlert.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            settingsPresenter.resetPassword(password.editText?.text.toString(), newPassword.editText?.text.toString(), conPassword.editText?.text.toString())
        }
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

    fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onSettingResponse(message: String) {
        Log.d("settingresponse", message)
    }

    override fun passwordInvalid(what: String) {
        when(what) {
            Constant.NEW_PASSWORD -> newPassword.error = getString(R.string.pass_validation_text)
            Constant.PASSWORD -> password.error = getString(R.string.pass_validation_text)
            Constant.CONFIRM_PASSWORD -> conPassword.error = getString(R.string.pass_validation_text)
        }
    }

    override fun invalidCredentials(isEmpty: Boolean, what: String) {
        if(isEmpty) {
            when(what) {
                Constant.PASSWORD -> password.error = getString(R.string.field_cannot_be_empty)
                Constant.NEW_PASSWORD -> newPassword.error = getString(R.string.field_cannot_be_empty)
                Constant.CONFIRM_PASSWORD -> conPassword.error = getString(R.string.field_cannot_be_empty)
            }
        } else {
            conPassword.error = getString(R.string.error_password_matching)
        }
    }

    override fun onResetPasswordResponse(message: String) {
        resetPasswordAlert.dismiss()
        if(!message.equals("null")) {
            showToast(message)
        } else {
            showToast(getString(R.string.wrong_password))
            showResetPasswordAlert()
        }
    }

    override fun checkUrl(isEmpty: Boolean) {
        if(isEmpty) {
            input_url.error = getString(R.string.field_cannot_be_empty)
        } else {
            input_url.error = getString(R.string.invalid_url)
        }
    }

    override fun setServerSuccessful() {
        setServerAlert.dismiss()
    }

    fun setupPasswordWatcher() {
        password.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            password.error = null
            if (!hasFocus)
                settingsPresenter.checkForPassword(password.editText?.text.toString(), Constant.PASSWORD)
        }
        newPassword.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            newPassword.error = null
            if (!hasFocus)
                settingsPresenter.checkForPassword(newPassword.editText?.text.toString(), Constant.NEW_PASSWORD)
        }

        conPassword.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            conPassword.error = null
            if (!hasFocus)
                settingsPresenter.checkForPassword(conPassword.editText?.text.toString(), Constant.CONFIRM_PASSWORD)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        settingsPresenter.onDetach()
    }
}