package org.fossasia.susi.ai.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.helper.MediaUtil;
import org.fossasia.susi.ai.helper.PrefManager;

import io.realm.Realm;

/**
 * <h1>The Settings activity.</h1>
 * <h2>This activity is used to define settings of the app. User can change them according to need.</h2>
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    /**
     * The Chat settings fragment.
     */
    public static class ChatSettingsFragment extends PreferenceFragmentCompat {
        private Preference textToSpeech,rate,server,micSettings;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.pref_settings);

            textToSpeech = (Preference) getPreferenceManager().findPreference(Constant.LANG_SELECT);
            textToSpeech.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent();
                    intent.setComponent( new ComponentName("com.android.settings","com.android.settings.Settings$TextToSpeechSettingsActivity" ));
                    startActivity(intent);
                    return true;
                }
            });

            rate=(Preference)getPreferenceManager().findPreference(Constant.RATE);
            rate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
                    return true;
                }
            });

            server=(Preference)getPreferenceManager().findPreference(Constant.SELECT_SERVER);
            server.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(Constant.CHANGE_SERVER);
                    builder.setMessage(Constant.SERVER_CHANGE_PROMPT)
                            .setCancelable(false)
                            .setNegativeButton(Constant.CANCEL,null)
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    PrefManager.putBoolean(Constant.SUSI_SERVER, true);
                                    PrefManager.clearToken();
                                    //clear all messages
                                    Realm realm = Realm.getDefaultInstance();
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            realm.deleteAll();
                                        }
                                    });
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return true;
                }
            });

            micSettings = getPreferenceManager().findPreference("Mic_input");
            if(ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                boolean voiceInputAvailable = MediaUtil.isAvailableForVoiceInput(getContext());
                if(!voiceInputAvailable)
                    PrefManager.putBoolean(Constant.MIC_INPUT, false);
                micSettings.setEnabled(voiceInputAvailable);
            } else {
                PrefManager.putBoolean(Constant.MIC_INPUT, false);
                micSettings.setEnabled(false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.finish();
    }
}
