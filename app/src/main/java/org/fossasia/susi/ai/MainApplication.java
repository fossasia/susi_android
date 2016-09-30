package org.fossasia.susi.ai;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by
 * --Vatsal Bajpai on
 * --30/09/16 at
 * --10:26 PM
 */

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // The Realm file will be located in Context.getFilesDir() with name "default.realm"
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
    }
}
