package org.fossasia.susi.ai.components;

import org.fossasia.susi.ai.MainApplication;
import org.fossasia.susi.ai.activities.MainActivity;
import org.fossasia.susi.ai.modules.NetworkModule;
import org.fossasia.susi.ai.modules.StorageModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by saurabh on 10/10/16.
 */
@Singleton
@Component(modules = {NetworkModule.class, StorageModule.class})
public interface SusiComponent {
    void inject(MainActivity mainActivity);

    void inject(MainApplication mainApplication);
}
