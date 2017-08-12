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
import android.widget.*
import kotlinx.android.synthetic.main.fragment_hotword_training.*
import org.fossasia.susi.ai.hotword.contract.IHotwordTrainingPresenter
import org.fossasia.susi.ai.hotword.contract.IHotwordTrainingView
import org.fossasia.susi.ai.settings.SettingsActivity

/**
 *
 * Created by chiragw15 on 9/8/17.
 */
class HotwordTrainingFragment: Fragment(), IHotwordTrainingView {

    lateinit var hotwordTrainingPresenter: IHotwordTrainingPresenter

    lateinit var waitingCircles: Array<ImageView>
    lateinit var progressBars: Array<ProgressBar>
    lateinit var ticks: Array<ImageView>
    lateinit var retryButtons: Array<Button>
    lateinit var listeningTexts: Array<TextView>

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


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

        waitingCircles = arrayOf(waitCircle1, waitCircle2, waitCircle3)
        progressBars = arrayOf(progressBar1, progressBar2, progressBar3)
        ticks = arrayOf(tick1, tick2, tick3)
        retryButtons = arrayOf(retryButton1, retryButton2, retryButton3)
        listeningTexts = arrayOf(listeningText1, listeningText2, listeningText3)

        setUpUI()
        addStartListener()
        hotwordTrainingPresenter = HotwordTrainingPresenter(activity as SettingsActivity)
        hotwordTrainingPresenter.onAttach(this)
        super.onViewCreated(view, savedInstanceState)
    }

    fun setUpUI() {
        hideProgressSpinners()
        hideTicks()
        hideRetryButtons()
        hideListeningTexts()
    }

    fun hideProgressSpinners() {
        for (progressBar in progressBars) progressBar.visibility = View.GONE
    }

    fun hideTicks() {
        for (tick in ticks) tick.visibility = View.GONE
    }

    fun hideRetryButtons() {
        for (retryButton in retryButtons) retryButton.visibility = View.GONE
    }

    fun hideListeningTexts() {
        for (listeningText in listeningTexts) listeningText.visibility = View.GONE
    }

    fun addStartListener() {
        startTrainingButton.setOnClickListener { hotwordTrainingPresenter.startHH }
    }

}