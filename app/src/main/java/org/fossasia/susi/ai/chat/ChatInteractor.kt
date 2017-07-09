package org.fossasia.susi.ai.chat

/**
 * Created by chiragw15 on 9/7/17.
 */
class ChatInteractor: IChatInteractor {
    override fun retrieveOldMessages(listener: IChatInteractor.OnRetrievingMessagesFinishedListener) {
        val thread = object : Thread() {
            override fun run() {
                getOldMessages(listener)
            }
        }
        thread.start()
    }

    fun getOldMessages(listener: IChatInteractor.OnRetrievingMessagesFinishedListener) {
    }
}