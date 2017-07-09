package org.fossasia.susi.ai.chat

/**
 * Created by chiragw15 on 9/7/17.
 */
interface IChatInteractor {

    interface OnRetrievingMessagesFinishedListener {
        fun onRetrieveError(message: String)
        fun onRetrieveSuccess(message: String)
        fun showRetrieveProgress()
        fun hideRetrieveProgress()
    }

    fun retrieveOldMessages(listener: OnRetrievingMessagesFinishedListener)
}