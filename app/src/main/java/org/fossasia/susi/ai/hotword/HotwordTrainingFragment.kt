package org.fossasia.susi.ai.hotword

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.fossasia.susi.ai.R
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.os.Build

/**
 *
 * Created by chiragw15 on 9/8/17.
 */
class HotwordTrainingFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.AppTheme)
        val localInflater = inflater?.cloneInContext(contextThemeWrapper)
        (activity as AppCompatActivity).supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(R.color.colorPrimary)
        }

        return localInflater!!.inflate(R.layout.fragment_hotword_training, container, false)
    }
}