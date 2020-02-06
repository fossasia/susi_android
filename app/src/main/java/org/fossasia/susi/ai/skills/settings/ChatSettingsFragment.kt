package org.fossasia.susi.ai.skills.settings

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v14.preference.SwitchPreference
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.widget.AppCompatCheckBox
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat
import java.util.Locale
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.device.DeviceActivity
import org.fossasia.susi.ai.device.DeviceActivity.Companion.CONNECT_TO
import org.fossasia.susi.ai.device.DeviceActivity.Companion.TAG_CONNECTED_DEVICE_FRAGMNENT
import org.fossasia.susi.ai.device.DeviceActivity.Companion.TAG_DEVICE_CONNECT_FRAGMENT
import org.fossasia.susi.ai.device.managedevices.ManageDeviceActivity
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.login.LoginActivity
import org.fossasia.susi.ai.login.LoginLogoutModulePresenter
import org.fossasia.susi.ai.login.contract.ILoginLogoutModulePresenter
import org.fossasia.susi.ai.skills.SkillsActivity
import org.fossasia.susi.ai.skills.aboutus.AboutUsFragment
import org.fossasia.susi.ai.skills.help.HelpFragment
import org.fossasia.susi.ai.skills.privacy.PrivacyFragment
import org.fossasia.susi.ai.skills.settings.contract.ISettingsPresenter
import org.fossasia.susi.ai.skills.settings.contract.ISettingsView
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import timber.log.Timber

/**
 * The Fragment for Settings Activity
 *
 * Created by mayanktripathi on 10/07/17.
 */

class ChatSettingsFragment : PreferenceFragmentCompat(), ISettingsView {

    private val TAG_ABOUT_FRAGMENT = "AboutUsFragment"
    private val TAG_HELP_FRAGMENT = "HelpFragment"
    private val TAG_PRIVACY_FRAGMENT = "PrivacyFragment"
    private val settingsPresenter: ISettingsPresenter by inject { parametersOf(this) }
    private lateinit var loginLogoutModulePresenter: ILoginLogoutModulePresenter

    private lateinit var rate: Preference
    lateinit var server: Preference
    private lateinit var micSettings: Preference
    private lateinit var hotwordSettings: Preference
    lateinit var share: Preference
    private lateinit var loginLogout: Preference
    private lateinit var aboutUs: Preference
    private lateinit var help: Preference
    private lateinit var privacy: Preference
    private lateinit var resetPassword: Preference
    private lateinit var enterSend: Preference
    private lateinit var speechAlways: SwitchPreference
    private lateinit var speechOutput: SwitchPreference
    private lateinit var displayEmail: Preference
    lateinit var password: TextInputLayout
    private lateinit var newPassword: TextInputLayout
    private lateinit var conPassword: TextInputLayout
    private lateinit var inputUrl: TextInputLayout
    private lateinit var resetPasswordAlert: AlertDialog
    private lateinit var setServerAlert: AlertDialog
    private lateinit var querylanguage: ListPreference
    private lateinit var deviceName: Preference
    private lateinit var setupDevice: Preference
    private lateinit var settingsVoice: Preference
    private lateinit var visitWebsite: Preference
    private lateinit var manageDevices: Preference
    private var flag = true
    private val packageName = "ai.susi"

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {

        addPreferencesFromResource(R.xml.pref_settings)

        val thisActivity = activity
        if (thisActivity is SkillsActivity) thisActivity.title = getString(R.string.action_settings)
        loginLogoutModulePresenter = LoginLogoutModulePresenter(requireContext())

        setHasOptionsMenu(true)

        rate = preferenceManager.findPreference(Constant.RATE)
        server = preferenceManager.findPreference(Constant.SELECT_SERVER)
        micSettings = preferenceManager.findPreference(getString(R.string.setting_mic_key))
        hotwordSettings = preferenceManager.findPreference(Constant.HOTWORD_DETECTION)
        share = preferenceManager.findPreference(Constant.SHARE)
        loginLogout = preferenceManager.findPreference(Constant.LOGIN_LOGOUT)
        aboutUs = preferenceManager.findPreference(getString(R.string.settings_about_us_key))
        help = preferenceManager.findPreference(getString(R.string.settings_help_key))
        privacy = preferenceManager.findPreference(getString(R.string.settings_privacy_key))
        resetPassword = preferenceManager.findPreference(Constant.RESET_PASSWORD)
        enterSend = preferenceManager.findPreference(getString(R.string.settings_enterPreference_key))
        speechOutput = preferenceManager.findPreference(getString(R.string.settings_speechPreference_key)) as SwitchPreference
        speechAlways = preferenceManager.findPreference(getString(R.string.settings_speechAlways_key)) as SwitchPreference
        displayEmail = preferenceManager.findPreference(getString(R.string.settings_displayEmail_key))
        querylanguage = preferenceManager.findPreference(Constant.LANG_SELECT) as ListPreference
        deviceName = preferenceManager.findPreference(Constant.DEVICE)
        setupDevice = preferenceManager.findPreference(Constant.DEVICE_SETUP)
        settingsVoice = preferenceManager.findPreference(Constant.VOICE_SETTINGS)
        visitWebsite = preferenceManager.findPreference(Constant.VISIT_WEBSITE)
        manageDevices = preferenceManager.findPreference(Constant.MANAGE_DEVICES)

        // Display login email
        val utilModel = UtilModel(activity as SkillsActivity)
        if (!utilModel.isLoggedIn()) {
            displayEmail.title = "Not logged in"
            displayEmail.isEnabled = true
            deviceName.isEnabled = false
            manageDevices.isEnabled = false
        } else {
            displayEmail.title = PrefManager.getStringSet(Constant.SAVED_EMAIL)?.iterator()?.next()
            displayEmail.isEnabled = false
            deviceName.isEnabled = true
            manageDevices.isEnabled = true
        }

        setLanguage()
        if (!utilModel.isLoggedIn()) {
            loginLogout.title = "Login"
        } else {
            loginLogout.title = "Logout"
        }

        if (PrefManager.token == null) {
            deviceName.isVisible = false
            setupDevice.isVisible = false
            manageDevices.isVisible = false
            preferenceManager.findPreference(getString(R.string.settings_deviceSection_key)).isVisible = false
        }

        querylanguage.setOnPreferenceChangeListener { _, newValue ->
            PrefManager.putString(Constant.LANGUAGE, newValue.toString())
            setLanguage()
            if (!settingsPresenter.getAnonymity()) {
                settingsPresenter.sendSetting(Constant.LANGUAGE, newValue.toString(), 1)
            }
            val intent = Intent(context, SkillsActivity::class.java)
            startActivity(intent)
            val context = this.context
            if (context is Activity) context.finish()
            true
        }
        rate.setOnPreferenceClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)))
            true
        }

        aboutUs.setOnPreferenceClickListener {
            val aboutFragment = AboutUsFragment()
            fragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_container, aboutFragment, TAG_ABOUT_FRAGMENT)
                    ?.addToBackStack(TAG_ABOUT_FRAGMENT)
                    ?.commit()
            true
        }

        help.setOnPreferenceClickListener {
            val helpFragment = HelpFragment()
            fragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_container, helpFragment, TAG_HELP_FRAGMENT)
                    ?.addToBackStack(TAG_HELP_FRAGMENT)
                    ?.commit()
            true
        }

        privacy.setOnPreferenceClickListener {
            val privacyFragment = PrivacyFragment()
            fragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_container, privacyFragment, TAG_PRIVACY_FRAGMENT)
                    ?.addToBackStack(TAG_PRIVACY_FRAGMENT)
                    ?.commit()
            true
        }
        share.setOnPreferenceClickListener {
            try {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        String.format(getString(R.string.promo_msg_template),
                                String.format(getString(R.string.app_share_url), packageName)))
                startActivity(shareIntent)
            } catch (e: Exception) {
                showToast(getString(R.string.error_msg_retry))
            }
            true
        }

        loginLogout.setOnPreferenceClickListener {
            if (utilModel.isLoggedIn()) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage(R.string.logout_confirmation).setCancelable(false).setPositiveButton(R.string.action_log_out) { _, _ ->
                    loginLogoutModulePresenter.logout()
                }.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }

                val alert = builder.create()
                alert.setTitle(getString(R.string.logout))
                alert.show()
            } else {
                loginLogoutModulePresenter.logout()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }
            true
        }

        visitWebsite.setOnPreferenceClickListener {
            try {
                val openWebsite = Intent(Intent.ACTION_VIEW, Uri.parse(Constant.SUSI_VISIT_WEBSITE))
                startActivity(openWebsite)
            } catch (e: ActivityNotFoundException) {
                showToast("No browser found. Please install a browser to continue.")
            }
            true
        }

        displayEmail.setOnPreferenceClickListener {
            loginLogoutModulePresenter.logout()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            true
        }

        setupDevice.setOnPreferenceClickListener {

            val intent = Intent(activity, DeviceActivity::class.java)
            intent.putExtra(CONNECT_TO, TAG_DEVICE_CONNECT_FRAGMENT)
            startActivity(intent)
            true
        }

        deviceName.setOnPreferenceClickListener {
            val intent = Intent(activity, DeviceActivity::class.java)
            intent.putExtra(CONNECT_TO, TAG_CONNECTED_DEVICE_FRAGMNENT)
            startActivity(intent)
            true
        }

        manageDevices.setOnPreferenceClickListener {
            val intent = Intent(activity, ManageDeviceActivity::class.java)
            startActivity(intent)
            true
        }

        settingsVoice.setOnPreferenceClickListener {
            val intent = Intent()
            intent.action = "com.android.settings.TTS_SETTINGS"
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
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

        if (!settingsPresenter.getAnonymity()) {
            resetPassword.isVisible = true
            resetPassword.setOnPreferenceClickListener {
                showResetPasswordAlert()
                true
            }
        } else {
            resetPassword.isVisible = false
        }

        micSettings.isEnabled = settingsPresenter.enableMic()

        hotwordSettings.isEnabled = settingsPresenter.enableHotword()

        if (!settingsPresenter.getAnonymity()) {
            micSettings.setOnPreferenceClickListener {
                settingsPresenter.sendSetting(getString(R.string.setting_mic_key), (PrefManager.getBoolean(R.string.setting_mic_key, false)).toString(), 1)
                true
            }

            enterSend.setOnPreferenceChangeListener { _, newValue ->
                settingsPresenter.sendSetting(getString(R.string.settings_enterPreference_key), newValue.toString(), 1)
                true
            }

            speechAlways.setOnPreferenceChangeListener { _, newValue ->
                settingsPresenter.sendSetting(getString(R.string.settings_speechAlways_key), newValue.toString(), 1)
                if (newValue.toString() == "true" && speechOutput.isChecked) {
                    speechOutput.isChecked = false
                    speechOutput.callChangeListener("false")
                }
                true
            }

            speechOutput.setOnPreferenceChangeListener { _, newValue ->
                settingsPresenter.sendSetting(getString(R.string.settings_speechPreference_key), newValue.toString(), 1)
                if (newValue.toString() == "true" && speechAlways.isChecked) {
                    speechAlways.isChecked = false
                    speechAlways.callChangeListener("false")
                }
                true
            }
        }
    }

    override fun onResume() {
        val thisActivity = activity
        if (thisActivity is SkillsActivity) thisActivity.title = getString(R.string.action_settings)
        super.onResume()
    }

    private fun setLanguage() {
        try {
            if (querylanguage.entries.isNotEmpty()) {
                val index = querylanguage.findIndexOfValue(PrefManager.getString(Constant.LANGUAGE, Constant.DEFAULT))
                querylanguage.setValueIndex(index)
                querylanguage.summary = querylanguage.entries[index]
                setLocalLanguage(PrefManager.getString(Constant.LANGUAGE, Constant.DEFAULT))
            }
        } catch (e: Exception) {
            Timber.e(e) // Language not present in app
            PrefManager.putString(Constant.LANGUAGE, Constant.DEFAULT)
            querylanguage.setValueIndex(0) // setting language to default - english
            querylanguage.summary = querylanguage.entries[0]
        }
    }

    private fun setLocalLanguage(Lang: String) {
        val locale = Locale(Lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        this.resources.updateConfiguration(config, this.resources.displayMetrics)
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(requireContext())
        val promptsView = activity?.layoutInflater?.inflate(R.layout.alert_change_server, null)
        inputUrl = promptsView?.findViewById(R.id.input_url) as TextInputLayout
        val inputUrlText = promptsView.findViewById(R.id.input_url_text) as TextInputEditText
        val customerServer = promptsView.findViewById(R.id.customer_server) as AppCompatCheckBox
        if (PrefManager.getBoolean(R.string.susi_server_selected_key, true)) {
            inputUrl.visibility = View.GONE
            flag = false
        } else {
            inputUrl.visibility = View.VISIBLE
            flag = true
        }
        customerServer.isChecked = flag
        inputUrlText.setText(PrefManager.getString(Constant.CUSTOM_SERVER, ""))
        customerServer.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                inputUrl.visibility = View.VISIBLE
            if (!isChecked)
                inputUrl.visibility = View.GONE
        }
        builder.setView(promptsView)
        builder.setTitle(Constant.CHANGE_SERVER)
                .setCancelable(false)
                .setNegativeButton(Constant.CANCEL, null)
                .setPositiveButton(activity?.getString(R.string.ok), null)
        setServerAlert = builder.create()
        setServerAlert.show()
        setServerAlert.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            settingsPresenter.setServer(customerServer.isChecked, inputUrl.editText?.text.toString())
        }
    }

    private fun showResetPasswordAlert() {
        val builder = AlertDialog.Builder(requireContext())
        val resetPasswordView = activity?.layoutInflater?.inflate(R.layout.alert_reset_password, null)
        password = resetPasswordView?.findViewById(R.id.password) as TextInputLayout
        newPassword = resetPasswordView.findViewById(R.id.newpassword) as TextInputLayout
        conPassword = resetPasswordView.findViewById(R.id.confirmpassword) as TextInputLayout
        builder.setView(resetPasswordView)
        builder.setTitle(Constant.CHANGE_PASSWORD)
                .setCancelable(false)
                .setNegativeButton(Constant.CANCEL, null)
                .setPositiveButton(getString(R.string.ok), null)
        resetPasswordAlert = builder.create()
        resetPasswordAlert.show()
        resetPasswordAlert.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager
                .LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        resetPasswordAlert.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        setupPasswordWatcher()
        resetPasswordAlert.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            settingsPresenter.resetPassword(password.editText?.text.toString(), newPassword.editText?.text.toString(), conPassword.editText?.text.toString())
        }
    }

    override fun micPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    override fun hotWordPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun startLoginActivity() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        activity?.finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onSettingResponse(message: String) {
        Timber.d(message)
    }

    override fun passwordInvalid(what: String) {
        when (what) {
            Constant.NEW_PASSWORD -> newPassword.error = getString(R.string.pass_validation_text)
            Constant.PASSWORD -> password.error = getString(R.string.pass_validation_text)
            Constant.CONFIRM_PASSWORD -> conPassword.error = getString(R.string.pass_validation_text)
        }
    }

    override fun invalidCredentials(isEmpty: Boolean, what: String) {
        if (isEmpty) {
            when (what) {
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
        if (message != "null") {
            showToast(message)
        } else {
            showToast(getString(R.string.wrong_password))
            showResetPasswordAlert()
        }
    }

    override fun checkUrl(isEmpty: Boolean) {
        if (isEmpty) {
            inputUrl.error = getString(R.string.field_cannot_be_empty)
        } else {
            inputUrl.error = getString(R.string.invalid_url)
        }
    }

    override fun setServerSuccessful() {
        setServerAlert.dismiss()
    }

    private fun setupPasswordWatcher() {
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
}
