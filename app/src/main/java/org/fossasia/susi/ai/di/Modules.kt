package org.fossasia.susi.ai.di

import org.fossasia.susi.ai.device.connecteddevices.ConnectedDevicePresenter
import org.fossasia.susi.ai.device.connecteddevices.contract.IConnectedDevicePresenter
import org.fossasia.susi.ai.device.connecteddevices.contract.IConnectedDeviceView
import org.fossasia.susi.ai.device.viewdevice.ViewDevicePresenter
import org.fossasia.susi.ai.device.viewdevice.contract.IViewDevicePresenter
import org.fossasia.susi.ai.device.viewdevice.contract.IViewDeviceView
import org.fossasia.susi.ai.skills.groupwiseskills.GroupWiseSkillsPresenter
import org.fossasia.susi.ai.skills.groupwiseskills.contract.IGroupWiseSkillsPresenter
import org.fossasia.susi.ai.skills.groupwiseskills.contract.IGroupWiseSkillsView
import org.koin.core.module.Module
import org.koin.dsl.module

val modules: Module = module(override = true) {

    factory<IGroupWiseSkillsPresenter> { (view: IGroupWiseSkillsView) -> GroupWiseSkillsPresenter(view) }

    factory<IConnectedDevicePresenter> { (view: IConnectedDeviceView) -> ConnectedDevicePresenter(view) }

    factory<IViewDevicePresenter> { (view: IViewDeviceView) -> ViewDevicePresenter(view) }
}
