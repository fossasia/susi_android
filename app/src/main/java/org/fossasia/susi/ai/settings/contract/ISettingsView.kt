package org.fossasia.susi.ai.settings.contract

/**
 * The interface for Settings view
 *
 * Created by mayanktripathi on 07/07/17.
 */

interface ISettingsView {
    fun startLoginActivity()
    fun micPermission(): Boolean
    fun hotWordPermission(): Boolean
}