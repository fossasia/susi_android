package org.fossasia.susi.ai.settings.contract

import org.fossasia.susi.ai.settings.ChatSettingsFragment

/**
 * The interface for Settings Presenter
 *
 * Created by mayanktripathi on 07/07/17.
 */

interface ISettingsPresenter {

    fun onAttach(chatSettingsFragment: ChatSettingsFragment)

    fun deleteMsg()

    fun enableMic(): Boolean

    fun enableHotword(): Boolean

    fun onDetach()

    fun getAnonymity(): Boolean

    fun login()

    fun logout()

}
