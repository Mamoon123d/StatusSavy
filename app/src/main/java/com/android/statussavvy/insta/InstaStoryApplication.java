package com.android.statussavvy.insta;

import android.app.Application;
import android.content.Context;

public class InstaStoryApplication extends Application {
    private static InstaStoryApplication instance;
    private static CookieManager mCookieManager;
    private static PreferencesHelper mPreferencesHelper;

    public static InstaStoryApplication getInstance() {
        return instance;
    }

    public void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }

    public void onCreate() {
        super.onCreate();
        instance = this;
//        OneSignal.startInit(this).inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification).unsubscribeWhenNotificationsAreDisabled(true).init();
//        new InitializeAdmobSdk(this, "ca-app-pub-4906547187531410~7793814716");
//        new InitiaizeFacebookSdk(this);
    }

    public synchronized CookieManager getCookieManager() {
        if (mCookieManager == null) {
            mCookieManager = new CookieManager(getApplicationContext());
        }
        return mCookieManager;
    }

    public synchronized PreferencesHelper getPreferencesHelper() {
        if (mPreferencesHelper == null) {
            mPreferencesHelper = new PreferencesHelper(getApplicationContext());
        }
        return mPreferencesHelper;
    }
}
