package org.fossasia.susi.ai.chat

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.Constant

/**
 * Created by chiragw15 on 9/7/17.
 */
class ChatActivity: AppCompatActivity(), IChatView  {

    var chatPresenter: IChatPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val firstRun = intent.getBooleanExtra(Constant.FIRST_TIME, false)

        chatPresenter = ChatPresenter()
        chatPresenter?.onAttach(this, firstRun, applicationContext)
    }

    override fun setTheme(darkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode( if(darkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }

    /*override fun showRetrieveOldMessageProgress() {
        val progressDialog = ProgressDialog(this@ChatActivity)
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.dialog_retrieve_messages_title))
        progressDialog.show()
    }*/

}