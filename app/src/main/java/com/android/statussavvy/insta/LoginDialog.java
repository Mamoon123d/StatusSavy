package com.android.statussavvy.insta;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.CookieManager;

import com.android.statussavvy.R;


public class LoginDialog implements View.OnClickListener {
    private static final String TAG_FB = "facebook";
    /* access modifiers changed from: private */
    public View btnclose;
    public Context context;
    /* access modifiers changed from: private */
    public View loadingProgress;
    /* access modifiers changed from: private */
    public Dialog loginDialog;
    /* access modifiers changed from: private */
    public com.android.statussavvy.insta.CookieManager mCookieManager;
    /* access modifiers changed from: private */
//    public FirebaseAnalytics mFirebaseAnalytics;
    /* access modifiers changed from: private */
    public OnLogInListner onLogInInterface;
    /* access modifiers changed from: private */
    public String siteurl;
    private WebChromeClient webChromeClient = new ChromeClient();
    /* access modifiers changed from: private */
    public WebView webview;

   public LoginDialog(Context context2, OnLogInListner onLogInListner) {
        this.context = context2;
        this.onLogInInterface = onLogInListner;
        initializeDialog();
//        this.mFirebaseAnalytics = firebaseAnalytics;
    }

    /* access modifiers changed from: package-private */
    public void showLoginDialog(String str) {
        this.siteurl = str;
        this.btnclose.setVisibility(View.GONE);
        this.loadingProgress.setVisibility(View.VISIBLE);
        this.webview.setVisibility(View.GONE);
        this.webview.loadUrl(str);
        this.loginDialog.show();
    }

    public void initializeDialog() {
        Dialog dialog = new Dialog(this.context);
        this.loginDialog = dialog;
        dialog.getWindow().requestFeature(1);
        this.loginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        this.loginDialog.setCanceledOnTouchOutside(true);
        this.loginDialog.setContentView(R.layout.login_dialog);
        this.loadingProgress = this.loginDialog.findViewById(R.id.loadingProgress);
        View findViewById = this.loginDialog.findViewById(R.id.btnClose);
        this.btnclose = findViewById;
        findViewById.setOnClickListener(this);
        WebView webView = (WebView) this.loginDialog.findViewById(R.id.loginWebView);
        this.webview = webView;
        webView.getSettings().setJavaScriptEnabled(true);
        this.webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.webview.getSettings().setLoadWithOverviewMode(true);
        this.webview.getSettings().setUseWideViewPort(true);
        this.webview.getSettings().setSupportZoom(true);
        this.webview.getSettings().setBuiltInZoomControls(true);
        this.webview.getSettings().setDatabaseEnabled(true);
        this.webview.getSettings().setDisplayZoomControls(false);
        this.webview.getSettings().setPluginState(WebSettings.PluginState.ON);
      //  this.webview.getSettings().setAppCacheEnabled(true);
        this.webview.getSettings().setAllowFileAccess(true);
        this.webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        this.webview.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            this.webview.getSettings().setMixedContentMode(0);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.webview.setLayerType(View.LAYER_TYPE_HARDWARE, (Paint) null);
        } else {
            this.webview.setLayerType(View.LAYER_TYPE_SOFTWARE, (Paint) null);
        }
        this.webview.setWebViewClient(new WebClient());
        this.webview.setWebChromeClient(this.webChromeClient);

        this.mCookieManager = InstaStoryApplication.getInstance().getCookieManager();
        if (Build.VERSION.SDK_INT <= 21) {
            this.webview.getSettings().setSavePassword(false);
        }

    }

    public void onClick(View view) {
        if (view.getId() == R.id.btnClose) {
            this.onLogInInterface.onLogInFailed();
        }
        this.loginDialog.dismiss();
    }

   public class ChromeClient extends WebChromeClient {
        ChromeClient() {
        }

        public void onProgressChanged(WebView webView, int i) {
            Log.e("TAG", "onProgressChanged: "+i );
            super.onProgressChanged(webView, i);
            if (i > 50 && LoginDialog.this.webview.getVisibility() == View.GONE) {
                LoginDialog.this.webview.setVisibility(View.VISIBLE);
                LoginDialog.this.btnclose.setVisibility(View.VISIBLE);
                LoginDialog.this.loadingProgress.setVisibility(View.GONE);
            }
        }
    }

    private class WebClient extends WebViewClient {
        private WebClient() {
        }

        public boolean shouldOverrideUrlLoading(WebView webView, String str) {
            return overrideUrlLoading(webView, str);
        }

        public boolean overrideUrlLoading(WebView webView, String str) {
            try {
                webView.loadUrl(str);
                String cookie = CookieManager.getInstance().getCookie(str);
                if (LoginDialog.this.mCookieManager.isValid(cookie)) {
                    Log.e("in overrideUrl", "on override Url" + cookie);
                    LoginDialog.this.mCookieManager.setCookie(cookie);
                    Bundle bundle = new Bundle();
                    bundle.putString("From", OnLogInListner.Instagram);
//                    LoginDialog.this.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
                    LoginDialog.this.onLogInInterface.onLogIn(OnLogInListner.Instagram);
                    webView.stopLoading();
                    LoginDialog.this.loginDialog.dismiss();
                    if (!LoginDialog.this.getFacebookCookie().booleanValue()) {
                        return true;
                    }
                    LoginDialog.this.onLogInInterface.onLogIn(OnLogInListner.Facebook);
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("From", TAG_FB);
//                    LoginDialog.this.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle2);
                    return true;
                } else if (!LoginDialog.this.getFacebookCookie().booleanValue() || !LoginDialog.this.siteurl.contains(TAG_FB)) {
                    return true;
                } else {
                    Bundle bundle3 = new Bundle();
                    bundle3.putString("From", TAG_FB);
//                    LoginDialog.this.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle3);
                    LoginDialog.this.onLogInInterface.onLogIn(OnLogInListner.Facebook);
                    webView.stopLoading();
                    LoginDialog.this.loginDialog.dismiss();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG", "overrideUrlLoading: "+e );
                return true;
            }
        }
    }

    /* access modifiers changed from: private */
    public Boolean getFacebookCookie() {
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.createInstance(this.context);
        }
        return Boolean.valueOf(CookieManager.getInstance().getCookie("https://m.facebook.com").contains("c_user"));
    }

    public static boolean hasAndroidMarket(Context context2) {
        return context2.getPackageManager().queryIntentActivities(new Intent("com.google.vending"), PackageManager.GET_RESOLVED_FILTER).size() > 0;
    }
}
