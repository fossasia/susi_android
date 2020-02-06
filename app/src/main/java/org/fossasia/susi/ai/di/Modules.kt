package org.fossasia.susi.ai.di

import org.fossasia.susi.ai.chat.ChatPresenter
import org.fossasia.susi.ai.chat.contract.IChatPresenter
import org.fossasia.susi.ai.chat.contract.IChatView
import org.fossasia.susi.ai.device.connecteddevices.ConnectedDevicePresenter
import org.fossasia.susi.ai.device.connecteddevices.contract.IConnectedDevicePresenter
import org.fossasia.susi.ai.device.connecteddevices.contract.IConnectedDeviceView
import org.fossasia.susi.ai.device.viewdevice.ViewDevicePresenter
import org.fossasia.susi.ai.device.viewdevice.contract.IViewDevicePresenter
import org.fossasia.susi.ai.device.viewdevice.contract.IViewDeviceView
import org.fossasia.susi.ai.login.LoginPresenter
import org.fossasia.susi.ai.login.contract.ILoginPresenter
import org.fossasia.susi.ai.login.contract.ILoginView
import org.fossasia.susi.ai.signup.SignUpPresenter
import org.fossasia.susi.ai.signup.contract.ISignUpPresenter
import org.fossasia.susi.ai.signup.contract.ISignUpView
import org.fossasia.susi.ai.skills.groupwiseskills.GroupWiseSkillsPresenter
import org.fossasia.susi.ai.skills.groupwiseskills.contract.IGroupWiseSkillsPresenter
import org.fossasia.susi.ai.skills.groupwiseskills.contract.IGroupWiseSkillsView
import org.fossasia.susi.ai.skills.settings.SettingsPresenter
import org.fossasia.susi.ai.skills.settings.contract.ISettingsPresenter
import org.fossasia.susi.ai.skills.settings.contract.ISettingsView
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val modules: Module = module(override = true) {

    factory<IGroupWiseSkillsPresenter> { (view: IGroupWiseSkillsView) -> GroupWiseSkillsPresenter(view) }

    factory<IConnectedDevicePresenter> { (view: IConnectedDeviceView) -> ConnectedDevicePresenter(view) }

    factory<IViewDevicePresenter> { (view: IViewDeviceView) -> ViewDevicePresenter(view) }

    factory<IChatPresenter> { (view: IChatView) -> ChatPresenter(androidContext(), view) }

    factory<ILoginPresenter> { (view: ILoginView) -> LoginPresenter(androidContext(), view) }

    factory<ISignUpPresenter> { (view: ISignUpView) -> SignUpPresenter(androidContext(), view) }

    factory<ISettingsPresenter> { (view: ISettingsView) -> SettingsPresenter(androidContext(), view) }
}
