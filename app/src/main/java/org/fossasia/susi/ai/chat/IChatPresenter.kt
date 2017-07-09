package org.fossasia.susi.ai.chat

import android.content.Context

/**
 * Created by chiragw15 on 9/7/17.
 */
interface IChatPresenter {

    fun onAttach(chatView: IChatView, firstRun: Boolean, context: Context)


}