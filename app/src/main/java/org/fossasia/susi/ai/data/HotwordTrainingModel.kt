package org.fossasia.susi.ai.data

import org.fossasia.susi.ai.data.contract.IHotwordTrainingModel

/**
 *
 * Created by chiragw15 on 14/8/17.
 */
class HotwordTrainingModel: IHotwordTrainingModel {
    override fun downloadModel(recordings: Array<String?>, listener: IHotwordTrainingModel.onHotwordTrainedFinishedListener) {
        listener.onTrainingFailure()
    }
}