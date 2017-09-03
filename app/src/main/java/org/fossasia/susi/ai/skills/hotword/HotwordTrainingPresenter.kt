package org.fossasia.susi.ai.skills.hotword

import android.net.Uri
import android.util.Log
import org.fossasia.susi.ai.data.HotwordTrainingModel
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.contract.IHotwordTrainingModel
import org.fossasia.susi.ai.data.contract.IUtilModel
import org.fossasia.susi.ai.skills.hotword.contract.IHotwordTrainingPresenter
import org.fossasia.susi.ai.skills.hotword.contract.IHotwordTrainingView
import org.fossasia.susi.ai.skills.SkillsActivity
import java.lang.Thread.sleep

/**
 * 
 * Created by chiragw15 on 9/8/17.
 */
class HotwordTrainingPresenter(skillsActivity: SkillsActivity): IHotwordTrainingPresenter,
        IHotwordTrainingModel.onHotwordTrainedFinishedListener, IUtilModel.onFFmpegCommandFinishedListener{

    var hotwordTrainingModel: IHotwordTrainingModel = HotwordTrainingModel()
    var utilModel: IUtilModel = UtilModel(skillsActivity)
    var hotwordTrainingView: IHotwordTrainingView?= null
    var BEFORE_RECORDING = 0
    var DURING_RECORDING = 1
    var AFTER_RECORDING = 2
    var currentState = 0
    val possibleSusiConf = arrayOf("SUSI", "SUZI", "SUSHI", "SUSIE", "SOOSI", "SOOZI", "SOOSHI", "SOOSIE")
    val recordingsUri = arrayOfNulls<Uri>(3)
    val recordingsEncodedString = arrayOfNulls<String>(3)

    override fun onAttach(hotwordTrainingView: IHotwordTrainingView) {
        this.hotwordTrainingView = hotwordTrainingView
    }

    override fun setUpUI() {
        currentState = BEFORE_RECORDING
        for(i in 0..2) {
            hotwordTrainingView?.visibilityProgressSpinners(false,i)
            hotwordTrainingView?.visibilityRetryButtons(false,i)
            hotwordTrainingView?.visibilityListeningTexts(false,i)
            hotwordTrainingView?.visibilityTicks(false,i)
        }
    }

    override fun startHotwordTraining(index: Int) {
        currentState = DURING_RECORDING
        hotwordTrainingView?.setListeningText("Listening... Say SUSI", index)
        hotwordTrainingView?.visibilityWaitingCircles(false, index)
        hotwordTrainingView?.visibilityRetryButtons(false, index)
        hotwordTrainingView?.visibilityListeningTexts(true, index)
        hotwordTrainingView?.visibilityProgressSpinners(true, index)
        sleep(1000)
        hotwordTrainingView?.startVoiceInput(index)
    }

    override fun speechInputSuccess(voiceResults: ArrayList<String>, index: Int, audioUri: Uri) {
        for (result in voiceResults) {
            var c = 0
            for(conf in possibleSusiConf) {
                if(result.toLowerCase() == conf.toLowerCase()) {
                    hotwordTrainingView?.visibilityProgressSpinners(false, index)
                    hotwordTrainingView?.visibilityTicks(true, index)
                    hotwordTrainingView?.setListeningText("Complete", index)
                    recordingsUri[index] = audioUri
                    c++
                    break
                }
            }
            if (c>0)
                break
        }
        if(index < 2) {
            startHotwordTraining(index + 1)
        } else {
            hotwordTrainingView?.setButtonText("Download Model")
            currentState = AFTER_RECORDING
        }
    }

    override fun speechInputFailure(index: Int) {
        hotwordTrainingView?.visibilityProgressSpinners(false, index)
        hotwordTrainingView?.visibilityWaitingCircles(true, index)
        hotwordTrainingView?.setListeningText("Didn't quite catch it. Try again.", index)
        hotwordTrainingView?.visibilityRetryButtons(true, index)
    }

    override fun startButtonClicked() {
        when(currentState) {
            BEFORE_RECORDING -> {
                startHotwordTraining(0)
                hotwordTrainingView?.setButtonText("Finish Later")
            }
            DURING_RECORDING -> {
                hotwordTrainingView?.exitFragment()
            }
            AFTER_RECORDING -> {
                hotwordTrainingView?.showProgress(true)

                utilModel.initializeFFmpeg()

                utilModel.copyAssetstoSD()

                for(i in 0..2) {
                    Log.v("chirag", recordingsUri[i].toString())
                }

                utilModel.convertAMRtoWAV(recordingsUri[0], 0, this)
            }
        }
    }

    override fun onCommandFinished(index: Int) {
        if(index+1 != 3) {
            utilModel.convertAMRtoWAV(recordingsUri[index+1], index+1, this)
        } else {
            for(i in 0..2)
                recordingsEncodedString[i] = utilModel.getEncodedString("recording$i")

            hotwordTrainingModel.downloadModel(recordingsEncodedString, this)
        }
    }

    override fun onTrainingSuccess() {
        hotwordTrainingView?.showProgress(false)

    }

    override fun onTrainingFailure() {
        hotwordTrainingView?.showProgress(false)
        hotwordTrainingView?.showToast("Could Not download model. Please Try again.")
    }
}