package org.fossasia.susi.ai.hotword

import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.hotword.contract.IHotwordTrainingPresenter
import org.fossasia.susi.ai.hotword.contract.IHotwordTrainingView
import org.fossasia.susi.ai.settings.SettingsActivity

/**
 * 
 * Created by chiragw15 on 9/8/17.
 */
class HotwordTrainingPresenter(settingsActivity: SettingsActivity): IHotwordTrainingPresenter {

    //var hotwordTrainingModel: HotwordTrainingModel = HotwordTrainingModel()
    var utilModel: UtilModel = UtilModel(settingsActivity)
    var hotwordTrainingView: IHotwordTrainingView?= null
    var submitu
    val possibleSusiConf = arrayOf("SUSI", "SUZI", "SUSHI", "SUSIE", "SOOSI", "SOOZI", "SOOSHI", "SOOSIE")

    override fun onAttach(hotwordTrainingView: IHotwordTrainingView) {
        this.hotwordTrainingView = hotwordTrainingView
    }

    override fun setUpUI() {
        for(i in 0..2) {
            hotwordTrainingView?.visibilityProgressSpinners(false,i)
            hotwordTrainingView?.visibilityRetryButtons(false,i)
            hotwordTrainingView?.visibilityListeningTexts(false,i)
            hotwordTrainingView?.visibilityTicks(false,i)
        }
    }

    override fun startHotwordTraining(index: Int) {
        hotwordTrainingView?.setListeningText("Listening... Say SUSI", index)
        hotwordTrainingView?.visibilityWaitingCircles(false, index)
        hotwordTrainingView?.visibilityRetryButtons(false, index)
        hotwordTrainingView?.visibilityListeningTexts(true, index)
        hotwordTrainingView?.visibilityProgressSpinners(true, index)
        hotwordTrainingView?.startVoiceInput(index)
    }

    override fun speechInputSuccess(voiceResults: ArrayList<String>, index: Int) {
        for (result in voiceResults) {
            var c = 0
            for(conf in possibleSusiConf) {
                if(result.toLowerCase() == conf.toLowerCase()) {
                    hotwordTrainingView?.visibilityProgressSpinners(false, index)
                    hotwordTrainingView?.visibilityTicks(true, index)
                    hotwordTrainingView?.setListeningText("Complete", index)
                    c++
                    break
                }
            }
            if (c>0)
                break
        }
        if(index < 2)
            startHotwordTraining(index+1)
        else {

        }
    }

    override fun speechInputFailure(index: Int) {
        hotwordTrainingView?.visibilityProgressSpinners(false, index)
        hotwordTrainingView?.visibilityWaitingCircles(true, index)
        hotwordTrainingView?.setListeningText("Didn't quite catch it. Try again.", index)
        hotwordTrainingView?.visibilityRetryButtons(true, index)
    }
}