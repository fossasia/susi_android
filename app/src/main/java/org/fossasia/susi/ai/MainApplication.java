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

    private static MainApplication instance;

    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        For Developers: Uncomment the below line after adding the API KEYS
//        for Instructions check the README.md
//        Fabric.with(this, new Crashlytics());
        instance = this;
        // The Realm file will be located in Context.getFilesDir() with name "default.realm"
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
    }
}
