package org.fossasia.susi.ai;

import android.app.Application;

import org.fossasia.susi.ai.components.DaggerSusiComponent;
import org.fossasia.susi.ai.components.SusiComponent;
import org.fossasia.susi.ai.modules.StorageModule;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by
 * --Vatsal Bajpai on
 * --30/09/16 at
 * --10:26 PM
 */

public class MainApplication extends Application {
    SusiComponent susiComponent;
    @Inject
    Realm realm;
    @Override
    public void onCreate() {
        super.onCreate();
        // The Realm file will be located in Context.getFilesDir() with name "default.realm"
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
        susiComponent = DaggerSusiComponent.builder()
                .storageModule(new StorageModule(this))
                .build();
        susiComponent.inject(this);
    }

    public SusiComponent getSusiComponent() {
        return susiComponent;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        realm.close();
    }
}
