package org.fossasia.susi.ai.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import org.fossasia.susi.ai.R;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    public static class ChatSettingsFragment extends PreferenceFragmentCompat {
        private Preference textToSpeech;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.pref_settings);

            textToSpeech = (Preference) getPreferenceManager().findPreference("Lang_Select");
            textToSpeech.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent();
                    intent.setComponent( new ComponentName("com.android.settings","com.android.settings.Settings$TextToSpeechSettingsActivity" ));
                    startActivity(intent);
                    return true;
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.finish();
    }
}
