package org.fossasia.susi.ai.settings

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.settings.contract.ISettingsPresenter

/**
 * The Fragment for Settings Activity
 *
 * Created by mayanktripathi on 10/07/17.
 */

class ChatSettingsFragment : PreferenceFragmentCompat() {

    var settingsPresenter: ISettingsPresenter? = null

    var textToSpeech: Preference? = null
    var rate: Preference? = null
    var server: Preference? = null
    var micSettings: Preference? = null
    var theme: ListPreference? = null
    var hotwordSettings: Preference? = null
    var settingActivity: SettingsActivity? = null
    var utilModel: UtilModel?= null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_settings)

        settingsPresenter = SettingsPresenter(activity)
        utilModel = UtilModel(activity)
        (settingsPresenter as SettingsPresenter).onAttach(this)

        textToSpeech = preferenceManager.findPreference(Constant.LANG_SELECT)
        rate = preferenceManager.findPreference(Constant.RATE)
        server = preferenceManager.findPreference(Constant.SELECT_SERVER)
        theme = preferenceManager.findPreference(Constant.THEME_KEY) as ListPreference
        micSettings = preferenceManager.findPreference(Constant.MIC_INPUT)
        hotwordSettings = preferenceManager.findPreference(Constant.HOTWORD_DETECTION)
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

        if(utilModel?.getAnonymity()!!){
            server?.isEnabled = true
            server?.setOnPreferenceClickListener {
                settingActivity?.showAlert(activity)
                true
            }
        }
        else {
            server?.isEnabled = false
        }

        theme?.setOnPreferenceChangeListener({ preference, newValue ->
            preference.summary = newValue.toString()
            settingsPresenter?.setTheme(newValue.toString())
            activity.recreate()
            true
        })

        micSettings?.isEnabled = settingsPresenter?.enableMic() as Boolean

        hotwordSettings?.isEnabled = settingsPresenter?.enableHotword() as Boolean
    }

    override fun onDestroyView() {
        super.onDestroyView()
        settingsPresenter?.onDetach()
    }

}
