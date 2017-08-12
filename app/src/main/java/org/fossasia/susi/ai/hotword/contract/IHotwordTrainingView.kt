package org.fossasia.susi.ai.hotword.contract

/**
 *
 * Created by chiragw15 on 9/8/17.
 */
interface IHotwordTrainingView {
    fun startVoiceInput(index: Int)
    fun visibilityProgressSpinners(visibility: Boolean, index: Int)
    fun visibilityTicks(visibility: Boolean, index: Int)
    fun visibilityRetryButtons(visibility: Boolean, index: Int)
    fun visibilityListeningTexts(visibility: Boolean, index: Int)
    fun visibilityWaitingCircles(visibility: Boolean, index: Int)
    fun setListeningText(message: String, index: Int)
}