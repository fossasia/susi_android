package org.fossasia.susi.ai.chat

import android.content.Context
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.NetworkUtils
import org.fossasia.susi.ai.helper.PrefManager


/**
 * Created by chiragw15 on 9/7/17.
 */
class ChatPresenter: IChatPresenter, IChatInteractor.OnRetrievingMessagesFinishedListener {

    var chatView: IChatView?= null
    var chatInteractor: IChatInteractor?= null

    override fun onAttach(chatView: IChatView, firstRun: Boolean, context: Context) {
        this.chatView = chatView
        this.chatInteractor = ChatInteractor()

        chatView.setTheme(PrefManager.getString(Constant.THEME, Constant.LIGHT) == Constant.DARK)

        if(firstRun and NetworkUtils().isNetworkConnected(context)) {
            chatInteractor?.retrieveOldMessages(this)
        }
    }

    override fun onRetrieveError(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRetrieveSuccess(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showRetrieveProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideRetrieveProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}