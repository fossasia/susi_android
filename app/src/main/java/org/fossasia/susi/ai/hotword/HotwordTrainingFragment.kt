package org.fossasia.susi.ai.hotword

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.fossasia.susi.ai.R

/**
 *
 * Created by cc15 on 9/8/17.
 */
class HotwordTrainingFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_hotword_training, container, false)
    }
}