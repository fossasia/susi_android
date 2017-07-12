package org.fossasia.susi.ai.chat

import android.Manifest
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.PrefManager

/**
 * Created by chiragw15 on 9/7/17.
 */
class ChatActivity: AppCompatActivity(), IChatView  {

    lateinit var chatPresenter: IChatPresenter
    val PERM_REQ_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val firstRun = intent.getBooleanExtra(Constant.FIRST_TIME, false)

        chatPresenter = ChatPresenter()
        chatPresenter.onAttach(this, applicationContext)
        chatPresenter.retrieveOldMessages(firstRun)
        chatPresenter.getLocationFromIP()

    }

    override fun setTheme(darkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode( if(darkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun checkPermissions(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun askForPermission(permissions: Array<String?>) {
        ActivityCompat.requestPermissions(this, permissions, PERM_REQ_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERM_REQ_CODE -> run {
                var audioPermissionGiven = false
                for (i in permissions.indices) {
                    when (permissions[i]) {
                        Manifest.permission.ACCESS_FINE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            //getLocationFromLocationService()
                            //locationHelper.getLocation()
                        }

                        Manifest.permission.RECORD_AUDIO -> {
                            if (grantResults.isNotEmpty() || grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                //micCheck = false
                                //PrefManager.putBoolean(Constant.MIC_INPUT, false)
                            } else {
                                //PrefManager.putBoolean(Constant.MIC_INPUT, PrefManager.checkMicInput(this))
                            }
                            audioPermissionGiven = true
                        }

                        Manifest.permission.WRITE_EXTERNAL_STORAGE -> if (grantResults.size >= 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED && audioPermissionGiven) {
                            if (Build.CPU_ABI.contains("arm") && !Build.FINGERPRINT.contains("generic") && PrefManager.checkMicInput(this))
                                //initHotword()
                            else {
                                //showToast(getString(R.string.error_hotword))
                                //PrefManager.putBoolean(Constant.HOTWORD_DETECTION, false)
                            }
                        }
                    }
                }
            }
        }
    }


    /*override fun showRetrieveOldMessageProgress() {
        val progressDialog = ProgressDialog(this@ChatActivity)
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.dialog_retrieve_messages_title))
        progressDialog.show()
    }*/

}