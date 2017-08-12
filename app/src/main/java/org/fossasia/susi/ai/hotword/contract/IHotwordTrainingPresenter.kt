package org.fossasia.susi.ai.hotword.contract

/**
 *
 * Created by chiragw15 on 9/8/17.
 */
interface IHotwordTrainingPresenter {
    fun onAttach(hotwordTrainingView: IHotwordTrainingView)
    fun startHotwordTraining(index: Int)
    fun speechInputSuccess(voiceResults: ArrayList<String>, index: Int)
    fun setUpUI()
}