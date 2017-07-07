package org.fossasia.susi.ai.settings

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.activities.MainActivity
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.login.LoginActivity

/**
 * Created by mayanktripathi on 07/07/17.
 */

class SettingsActivity : AppCompatActivity(), ISettingsView {

    var settingsPresenter: ISettingsPresenter? = null
    var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences(Constant.THEME, Context.MODE_PRIVATE)

        settingsPresenter = SettingsPresenter()
        settingsPresenter?.onAttach(this, prefs as SharedPreferences)

        if (settingsPresenter?.getThemes() == "Dark") {
            setTheme(R.style.PreferencesThemeDark)
        } else {
            setTheme(R.style.PreferencesThemeLight)
        }

        setContentView(R.layout.activity_settings)
    }

    class ChatSettingsFragment : PreferenceFragmentCompat() {

        var textToSpeech: Preference? = null
        var rate: Preference? = null
        var server: Preference? = null
        var micSettings: Preference? = null
        var theme: ListPreference? = null
        var settingActivity: SettingsActivity? = null

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.pref_settings)

            textToSpeech = preferenceManager.findPreference(Constant.LANG_SELECT)
            rate = preferenceManager.findPreference(Constant.RATE)
            server = preferenceManager.findPreference(Constant.SELECT_SERVER)
            theme = preferenceManager.findPreference(Constant.THEME_KEY) as ListPreference
            micSettings = preferenceManager.findPreference(Constant.MIC_INPUT)
            settingActivity = SettingsActivity()

            if (theme?.value == null)
                theme?.setValueIndex(1)

            if (theme?.entry != null)
                theme?.summary = theme?.entry.toString()

            textToSpeech?.setOnPreferenceClickListener {
                val intent = Intent()
                intent.component = ComponentName("com.android.settings", "com.android.settings.Settings\$TextToSpeechSettingsActivity")
                startActivity(intent)
                true
            }

            rate?.setOnPreferenceClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)))
                true
            }

            server?.setOnPreferenceClickListener {
                settingActivity?.showAlert(activity)
                true
            }

            theme?.setOnPreferenceChangeListener({ preference, newValue ->
                preference.summary = newValue.toString()
                settingActivity?.setTheme(newValue.toString())
                activity.recreate()
                true
            })

            micSettings?.setOnPreferenceChangeListener({ _, _ ->
                micSettings?.isEnabled = settingActivity?.enableMic()!!
                true
                })
        }
    }

    override fun showAlert(activity: FragmentActivity) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(Constant.CHANGE_SERVER)
        builder.setMessage(Constant.SERVER_CHANGE_PROMPT)
                .setCancelable(false)
                .setNegativeButton(Constant.CANCEL, null)
                .setPositiveButton(activity.getString(R.string.ok)) { dialog, _ ->
                    settingsPresenter?.deleteMsg()
                    val intent = Intent(activity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    activity.startActivity(intent)
                    dialog.dismiss()
                }
        val alert = builder.create()
        alert.show()
    }

    override fun onBackPressed() {
        super.finish()
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun enableMic(): Boolean? {
        return settingsPresenter?.enableMic(this)
    }

    override fun setTheme(string: String) {
        settingsPresenter?.setTheme(string)
    }

}
