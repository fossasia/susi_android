package org.fossasia.susi.ai.skills.privacy

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.signup.SignUpActivity

class PrivacyFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val thisActivity = activity
        thisActivity?.title = getString(R.string.settings_privacy)
        val rootView = inflater.inflate(R.layout.fragment_privacy, container, false)
        setHasOptionsMenu(true)

        val accept_terms = rootView.findViewById<CheckBox>(R.id.acceptTermsAndConditionsPrivacyFragment)

        if (PrefManager.getBoolean(R.string.accepted_terms_and_conditions, false)) {
            accept_terms.setChecked(true)
            accept_terms.setEnabled(false)
        }

        accept_terms.setOnCheckedChangeListener { buttonView, isChecked ->
            PrefManager.putBoolean(R.string.accepted_terms_and_conditions, true)
            accept_terms.setChecked(true)
            accept_terms.setEnabled(false)
            val intent = Intent(context, SignUpActivity::class.java)
            startActivity(intent)
        }

        return rootView
    }
}
