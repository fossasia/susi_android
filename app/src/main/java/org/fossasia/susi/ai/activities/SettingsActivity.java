package org.fossasia.susi.ai.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.helper.CredentialHelper;
import org.fossasia.susi.ai.helper.MediaUtil;
import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.login.LoginActivity;

import io.realm.Realm;

import static org.fossasia.susi.ai.helper.Constant.DARK;

/**
 * <h1>The Settings activity.</h1>
 * <h2>This activity is used to define settings of the app. User can change them according to need.</h2>
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private static SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(Constant.THEME, MODE_PRIVATE);
        Log.d(TAG, "onCreate: " + (prefs.getString(Constant.THEME, DARK)));
        if(prefs.getString(Constant.THEME, "Light").equals("Dark")) {
            setTheme(R.style.PreferencesThemeDark);
        }
        else {
            setTheme(R.style.PreferencesThemeLight);
        }
        setContentView(R.layout.activity_settings);

    }

    /**
     * The Chat settings fragment.
     */
    public static class ChatSettingsFragment extends PreferenceFragmentCompat {
        private Preference textToSpeech,rate,server,micSettings, hotwordSettings;
        private ListPreference theme;
        private RadioButton susi_server, personal_server;
        private TextInputLayout url;
        private TextInputEditText urlText;

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
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    if (PrefManager.getBoolean(Constant.ANONYMOUS_LOGGED_IN, false)) {
                        boolean flag;
                        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                        View promptsView = layoutInflater.inflate(R.layout.alert_change_server, null);
                        susi_server = (RadioButton) promptsView.findViewById(R.id.susi_default);
                        personal_server = (RadioButton) promptsView.findViewById(R.id.personal_server);
                        url = (TextInputLayout) promptsView.findViewById(R.id.input_url);
                        urlText = (TextInputEditText) promptsView.findViewById(R.id.input_url_text);
                        if(PrefManager.getBoolean(Constant.SUSI_SERVER, false)) {
                            url.setVisibility(View.GONE);
                            flag = true;
                        } else {
                            url.setVisibility(View.VISIBLE);
                            flag = false;
                        }
                        susi_server.setChecked(flag);
                        personal_server.setChecked(!flag);
                        urlText.setText(PrefManager.getString(Constant.CUSTOM_SERVER, null));
                        susi_server.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                url.setVisibility(View.GONE);
                            }
                        });
                        personal_server.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                url.setVisibility(View.VISIBLE);
                            }
                        });
                        builder.setView(promptsView);
                    } else {
                        builder.setMessage(Constant.SERVER_CHANGE_PROMPT);
                    }
                    builder.setTitle(Constant.CHANGE_SERVER);
                    builder.setCancelable(false)
                            .setNegativeButton(Constant.CANCEL, null)
                            .setPositiveButton(getString(R.string.ok), null);
                    final AlertDialog alert = builder.create();
                    alert.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(final DialogInterface dialog) {
                            Button button = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (PrefManager.getBoolean(Constant.ANONYMOUS_LOGGED_IN, false)) {
                                        if (personal_server.isChecked()) {
                                            if (!CredentialHelper.checkIfEmpty(url, getContext()) && CredentialHelper.isURLValid(url.getEditText().getText().toString())) {
                                                if (CredentialHelper.getValidURL(url.getEditText().getText().toString()) != null) {
                                                    PrefManager.putBoolean(Constant.SUSI_SERVER, false);
                                                    PrefManager.putString(Constant.CUSTOM_SERVER, CredentialHelper.getValidURL(url.getEditText().getText().toString()));
                                                    dialog.dismiss();
                                                } else {
                                                    url.setError(getContext().getString(R.string.invalid_url));
                                                }
                                            }
                                        } else {
                                            PrefManager.putBoolean(Constant.SUSI_SERVER, true);
                                            dialog.dismiss();
                                        }
                                    } else {
                                        PrefManager.putBoolean(Constant.SUSI_SERVER, true);
                                        PrefManager.clearToken();
                                        PrefManager.putBoolean(Constant.ANONYMOUS_LOGGED_IN, false);
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
                                }
                            });
                        }
                    });
                    alert.show();
                    return true;
                }
            });

            theme = (ListPreference) getPreferenceManager().findPreference(getString(R.string.theme_setting_key));
            if(theme.getValue()==null) {
                theme.setValueIndex(1);
            }
            if(theme.getEntry() != null)
                theme.setSummary(theme.getEntry().toString());

            theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(newValue.toString());
                    Log.d(TAG, "onPreferenceChange: " + newValue.toString());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(Constant.THEME, newValue.toString());
                    editor.apply();
                    Log.d(TAG, "onPreferenceChange: " + prefs.getString(Constant.THEME, DARK));
                    getActivity().recreate();
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

            hotwordSettings = getPreferenceManager().findPreference("hotword_detection");
            if(ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                boolean voiceInputAvailable = MediaUtil.isAvailableForVoiceInput(getContext());
                if(!voiceInputAvailable || !Build.CPU_ABI.contains("arm") || Build.FINGERPRINT.contains("generic")) {
                    PrefManager.putBoolean(Constant.HOTWORD_DETECTION, false);
                    hotwordSettings.setEnabled(false);
                } else {
                    hotwordSettings.setEnabled(true);
                }
            } else {
                PrefManager.putBoolean(Constant.HOTWORD_DETECTION, false);
                hotwordSettings.setEnabled(false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.finish();
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}