package org.fossasia.susi.ai.login

import android.content.Context
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.db.DatabaseRepository
import org.fossasia.susi.ai.data.db.contract.IDatabaseRepository
import org.fossasia.susi.ai.login.contract.ILoginLogoutModulePresenter
import org.fossasia.susi.ai.skills.settings.contract.ISettingsView

class LoginLogoutModulePresenter(context: Context) : ILoginLogoutModulePresenter {

    private val utilModel: UtilModel = UtilModel(context)
    private val databaseRepository: IDatabaseRepository = DatabaseRepository()
    private val settingView: ISettingsView? = null

    override fun logout() {
        utilModel.clearToken()
        utilModel.clearPrefs()
        utilModel.saveAnonymity(false)
        databaseRepository.deleteAllMessages()
        settingView?.startLoginActivity()
    }
}
