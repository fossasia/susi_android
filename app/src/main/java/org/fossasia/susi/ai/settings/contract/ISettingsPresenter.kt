package org.fossasia.susi.ai.settings.contract

/**
 * The interface for Settings Presenter
 *
 * Created by mayanktripathi on 07/07/17.
 */

interface ISettingsPresenter {

    fun onAttach(settingsView: ISettingsView)

    fun enableMic(): Boolean

    fun enableHotword(): Boolean

    fun onDetach()

    fun getAnonymity(): Boolean

    fun loginLogout()

}
