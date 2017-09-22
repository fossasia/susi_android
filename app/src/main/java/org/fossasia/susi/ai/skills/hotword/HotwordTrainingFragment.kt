package org.fossasia.susi.ai.skills.hotword

import android.app.ProgressDialog
import android.content.Intent
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
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.*
import kotlinx.android.synthetic.main.fragment_hotword_training.*
import org.fossasia.susi.ai.skills.hotword.contract.IHotwordTrainingPresenter
import org.fossasia.susi.ai.skills.hotword.contract.IHotwordTrainingView
import android.app.Activity
import android.util.Log
import org.fossasia.susi.ai.skills.SkillsActivity

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
    lateinit var progressDialog: ProgressDialog
    lateinit var recognizer: SpeechRecognizer

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

        hotwordTrainingPresenter = HotwordTrainingPresenter(activity as SkillsActivity)
        hotwordTrainingPresenter.onAttach(this)
        recognizer = SpeechRecognizer.createSpeechRecognizer(activity.applicationContext)
        hotwordTrainingPresenter.setUpUI()

        progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Downloading Model")

        addStartListener()
        addRetryListener()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun visibilityProgressSpinners(visibility: Boolean, index: Int) {
        if(visibility) progressBars[index].visibility = View.VISIBLE else progressBars[index].visibility = View.GONE
    }

    override fun visibilityTicks(visibility: Boolean, index: Int) {
        if(visibility) ticks[index].visibility = View.VISIBLE else ticks[index].visibility = View.GONE
    }

    override fun visibilityRetryButtons(visibility: Boolean, index: Int) {
        if(visibility) retryButtons[index].visibility = View.VISIBLE else retryButtons[index].visibility = View.GONE
    }

    override fun visibilityListeningTexts(visibility: Boolean, index: Int) {
        if(visibility) listeningTexts[index].visibility = View.VISIBLE else listeningTexts[index].visibility = View.GONE
    }

    override fun visibilityWaitingCircles(visibility: Boolean, index: Int) {
        if(visibility) waitingCircles[index].visibility = View.VISIBLE else waitingCircles[index].visibility = View.GONE
    }

    override fun setListeningText(message: String, index: Int) {
        listeningTexts[index].text = message
    }

    fun addStartListener() {
        startTrainingButton.setOnClickListener {
            hotwordTrainingPresenter.startButtonClicked()
        }
    }

    override fun setButtonText(text: String) {
        startTrainingButton.text = text
    }

    fun addRetryListener() {
        for ( i in 0..2 ) {
            retryButtons[i].setOnClickListener { hotwordTrainingPresenter.startHotwordTraining(i) }
        }
    }

    override fun startVoiceInput(index: Int) {

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                "com.domain.app")
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        intent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR")
        intent.putExtra("android.speech.extra.GET_AUDIO", true)

        startActivityForResult(intent, index);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        if(resultCode == Activity.RESULT_OK) {
            val audioUri = data.data
            Log.v("chirag",audioUri.toString())
            val bundle = data.extras
            val matches = bundle.getStringArrayList(RecognizerIntent.EXTRA_RESULTS)
            hotwordTrainingPresenter.speechInputSuccess(matches, requestCode, audioUri)
        } else {
            hotwordTrainingPresenter.speechInputFailure(requestCode)
        }
    }

    override fun showProgress(boolean: Boolean) {
        if (boolean) progressDialog.show() else progressDialog.dismiss()
    }

    override fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun exitFragment() {
        activity.onBackPressed()
    }
}