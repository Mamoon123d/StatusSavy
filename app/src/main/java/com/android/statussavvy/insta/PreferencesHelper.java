package com.android.statussavvy.insta;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {
    private SharedPreferences mPref;

    public PreferencesHelper(Context context) {
        this.mPref = context.getSharedPreferences("storyCookie", 0);
    }

    /* access modifiers changed from: package-private */
    public String getString(String str) {
        return this.mPref.getString(str, (String) null);
    }

    /* access modifiers changed from: package-private */
    public void putString(String str, String str2) {
        this.mPref.edit().putString(str, str2).apply();
    }
}
