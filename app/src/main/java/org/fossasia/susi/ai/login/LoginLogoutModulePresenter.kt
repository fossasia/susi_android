package org.fossasia.susi.ai.login

import android.app.Activity
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.db.DatabaseRepository
import org.fossasia.susi.ai.data.db.contract.IDatabaseRepository
import org.fossasia.susi.ai.login.contract.ILoginLogoutModulePresenter
import org.fossasia.susi.ai.skills.settings.contract.ISettingsView

class LoginLogoutModulePresenter(activeActivity: Activity):ILoginLogoutModulePresenter {

    private var utilModel: UtilModel = UtilModel(activeActivity)
    private var databaseRepository: IDatabaseRepository = DatabaseRepository()
    private var settingView: ISettingsView? = null

    override fun loginLogout() {
        utilModel.clearToken()
        utilModel.clearPrefs()
        utilModel.saveAnonymity(false)
        databaseRepository.deleteAllMessages()
        settingView?.startLoginActivity()
    }
}