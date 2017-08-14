package org.fossasia.susi.ai.hotword.contract

import android.net.Uri

/**
 *
 * Created by chiragw15 on 9/8/17.
 */
interface IHotwordTrainingPresenter {
    fun onAttach(hotwordTrainingView: IHotwordTrainingView)
    fun setUpUI()
    fun startHotwordTraining(index: Int)
    fun speechInputSuccess(voiceResults: ArrayList<String>, index: Int, audioUri: Uri)
    fun speechInputFailure(index: Int)
    fun startButtonClicked()
}