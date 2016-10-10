package org.fossasia.susi.ai.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

/**
 * Created by saurabh on 10/10/16.
 */
@Module
public class StorageModule {
    Context context;

    public StorageModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    @Provides
    @Singleton
    public SharedPreferences getDefaultPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
