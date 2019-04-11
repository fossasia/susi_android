package org.fossasia.susi.ai.login

import android.content.Context
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.db.DatabaseRepository
import org.fossasia.susi.ai.data.db.contract.IDatabaseRepository
import org.fossasia.susi.ai.login.contract.ILoginLogoutModulePresenter
import org.fossasia.susi.ai.skills.settings.contract.ISettingsView

class LoginLogoutModulePresenter(context: Context) : ILoginLogoutModulePresenter {

    private var utilModel: UtilModel = UtilModel(context)
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