package com.android.statussavvy.activity

import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import android.webkit.*
import android.widget.Toast
import com.android.statussavvy.R
import com.android.statussavvy.base.BaseActivity
import com.android.statussavvy.databinding.WebBinding
import java.io.File


class Web : BaseActivity<WebBinding>() {
    override fun setLayoutId(): Int {
        return R.layout.web
    }

    override fun initM() {
     setWeb()
    }

    private fun setWeb() {
        val webView=binding.webView
        val url=""
        webView.settings.javaScriptEnabled = true
        webView.settings.setPluginState(WebSettings.PluginState.ON);
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.addJavascriptInterface(this, "FBDownloader")
        processVideo("","yWDuG2&fs")
        val viewClient=object :WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webView.loadUrl("javascript:(function() { "
                        + "var el = document.querySelectorAll('div[data-sigil]');"
                        + "for(var i=0;i<el.length; i++)"
                        + "{"
                        + "var sigil = el[i].dataset.sigil;"
                        + "if(sigil.indexOf('inlineVideo') > -1){"
                        + "delete el[i].dataset.sigil;"
                        + "var jsonData = JSON.parse(el[i].dataset.store);"
                        + "el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\");');"
                        + "}" + "}" + "})()");
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                webView.loadUrl("javascript:(function prepareVideo() { "
                        + "var el = document.querySelectorAll('div[data-sigil]');"
                        + "for(var i=0;i<el.length; i++)"
                        + "{"
                        + "var sigil = el[i].dataset.sigil;"
                        + "if(sigil.indexOf('inlineVideo') > -1){"
                        + "delete el[i].dataset.sigil;"
                        + "console.log(i);"
                        + "var jsonData = JSON.parse(el[i].dataset.store);"
                        + "el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');"
                        + "}" + "}" + "})()");
                webView.loadUrl("javascript:( window.onload=prepareVideo;"
                        + ")()")
            }

        }
         webView.webViewClient=viewClient
        CookieSyncManager.createInstance(this)
        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        CookieSyncManager.getInstance().startSync()

        webView.loadUrl("https://m.facebook.com/")


    }


    @JavascriptInterface
    fun processVideo(vidData: String?, vidID: String) {
        try {
            val mBaseFolderPath: String = (Environment.getExternalStorageDirectory().absolutePath+File.separator).toString() + "FacebookVideos" + File.separator
            if (!File(mBaseFolderPath).exists()) {
                File(mBaseFolderPath).mkdir()
            }
            val mFilePath = "file://$mBaseFolderPath/$vidID.mp4"
            val downloadUri: Uri = Uri.parse(vidData)
            val req: DownloadManager.Request = DownloadManager.Request(downloadUri)
            req.setDestinationUri(Uri.parse(mFilePath))
            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val dm: DownloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(req)
            Toast.makeText(this, "Download Started", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Download Failed: $e", Toast.LENGTH_LONG).show()
        }
    }
}