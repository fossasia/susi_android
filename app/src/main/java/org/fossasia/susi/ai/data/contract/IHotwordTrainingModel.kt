package org.fossasia.susi.ai.data.contract

/**
 *
 * Created by chiragw15 on 14/8/17.
 */
interface IHotwordTrainingModel {
    interface onHotwordTrainedFinishedListener {
        fun onTrainingSuccess()
        fun onTrainingFailure()
    }

    fun downloadModel(recordings: Array<String?>, listener: onHotwordTrainedFinishedListener)
}