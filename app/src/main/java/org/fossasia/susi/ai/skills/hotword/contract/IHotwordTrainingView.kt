package org.fossasia.susi.ai.skills.hotword.contract

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
    fun setButtonText(text: String)
    fun exitFragment()
    fun showProgress(boolean: Boolean)
    fun showToast(message: String)
}