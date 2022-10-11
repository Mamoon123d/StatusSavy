package com.digital.statussavvy.insta;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.ValueCallback;

import androidx.annotation.RequiresApi;


import java.lang.ref.WeakReference;

public class CookieManager {
    private static String COOKIE_KEY = "storyCookie";
    private WeakReference<Context> mContext;
    private final PreferencesHelper mPreferencesHelper = InstaStoryApplication.getInstance().getPreferencesHelper();

    public CookieManager(Context context) {
        this.mContext = new WeakReference<>(context);
    }

    public String getCookie() {
        Log.e("cookie", "cookie " + this.mPreferencesHelper.getString(COOKIE_KEY));
        return this.mPreferencesHelper.getString(COOKIE_KEY);
    }

    /* access modifiers changed from: package-private */
    public void removeIgramCookie(String str) {
        clearCookies(str);
        this.mPreferencesHelper.putString(COOKIE_KEY, (String) null);
    }

    /* access modifiers changed from: package-private */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void removefbCookie(String str) {
        android.webkit.CookieManager instance = android.webkit.CookieManager.getInstance();
        instance.removeAllCookies((ValueCallback) null);
        instance.flush();
    }

    /* access modifiers changed from: package-private */
    public void setCookie(String str) {
        this.mPreferencesHelper.putString(COOKIE_KEY, str);
    }

    private void clearCookies(String str) {
        android.webkit.CookieManager instance = android.webkit.CookieManager.getInstance();
        String cookie = instance.getCookie(str);
        if (cookie != null) {
            for (String split : cookie.split(";")) {
                String[] split2 = split.split("=");
                instance.setCookie(str, split2[0].trim() + "=; Expires=Wed, 31 Dec 2025 23:59:59 GMT");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isValid() {
        return isValid(getCookie());
    }

    /* access modifiers changed from: package-private */
    public boolean isValid(String str) {
        return str != null && str.contains("sessionid");
    }
}
