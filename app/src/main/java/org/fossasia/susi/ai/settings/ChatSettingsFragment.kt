package org.fossasia.susi.ai.settings

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.widget.AppCompatCheckBox
import android.util.Log
import android.view.View
import android.widget.Toast
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.CredentialHelper
import org.fossasia.susi.ai.login.LoginActivity
import org.fossasia.susi.ai.helper.PrefManager
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
    lateinit var enterSend: Preference
    lateinit var speechAlways: Preference
    lateinit var speechOutput: Preference
    var flag = true

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
        enterSend = preferenceManager.findPreference(Constant.ENTER_SEND)
        speechOutput = preferenceManager.findPreference(Constant.SPEECH_OUTPUT)
        speechAlways = preferenceManager.findPreference(Constant.SPEECH_ALWAYS)

        if (settingsPresenter.getAnonymity()) {
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

        if (settingsPresenter.getAnonymity()) {
            server.isEnabled = true
            server.setOnPreferenceClickListener {
                showAlert()
                true
            }
        } else {
            server.isEnabled = false
        }

        micSettings.isEnabled = settingsPresenter.enableMic()

        hotwordSettings.isEnabled = settingsPresenter.enableHotword()

        if(!settingsPresenter.getAnonymity()) {
            micSettings.setOnPreferenceClickListener {
                settingsPresenter.sendSetting(Constant.MIC_INPUT, (PrefManager.getBoolean(Constant.MIC_INPUT, false)).toString())
                true
            }

            enterSend.setOnPreferenceChangeListener { _, newValue ->
                settingsPresenter.sendSetting(Constant.ENTER_SEND, newValue.toString())
                true
            }

            speechAlways.setOnPreferenceChangeListener { _, newValue ->
                settingsPresenter.sendSetting(Constant.SPEECH_ALWAYS, newValue.toString())
                true
            }

            speechOutput.setOnPreferenceChangeListener { _, newValue ->
                settingsPresenter.sendSetting(Constant.SPEECH_OUTPUT, newValue.toString())
                true
            }
        }
    }

    fun showAlert() {
        val builder = AlertDialog.Builder(activity)
        val promptsView = activity.layoutInflater.inflate(R.layout.alert_change_server, null)
        val input_url = promptsView.findViewById(R.id.input_url) as TextInputLayout
        val input_url_text = promptsView.findViewById(R.id.input_url_text) as TextInputEditText
        val customer_server = promptsView.findViewById(R.id.customer_server) as AppCompatCheckBox
        if (PrefManager.getBoolean(Constant.SUSI_SERVER, false)) {
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
                .setPositiveButton(activity.getString(R.string.ok)) { dialog, _ ->
                    if (customer_server.isChecked) {
                        if (!CredentialHelper.checkIfEmpty(input_url, activity) && CredentialHelper.isURLValid(input_url, activity)) {
                            if (CredentialHelper.getValidURL(input_url.editText?.text.toString()) != null) {
                                PrefManager.putBoolean(Constant.SUSI_SERVER, false)
                                PrefManager.putString(Constant.CUSTOM_SERVER, CredentialHelper.getValidURL(input_url.editText?.text.toString()))
                                dialog.dismiss()
                            } else {
                                input_url.error = this.getString(R.string.invalid_url)
                            }
                        }
                    } else {
                        PrefManager.putBoolean(Constant.SUSI_SERVER, true)
                        dialog.dismiss()
                    }

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

    fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onSettingResponse(message: String) {
        Log.d("settingresponse", message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        settingsPresenter.onDetach()
    }
}