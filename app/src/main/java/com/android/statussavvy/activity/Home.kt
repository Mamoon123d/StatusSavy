package com.android.statussavvy.activity

import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.app.Dialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.storage.StorageManager
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.statussavvy.NotificationHelper
import com.android.statussavvy.R
import com.android.statussavvy.adapter.InstaStoryAdapter
import com.android.statussavvy.adapter.StatusAdapter
import com.android.statussavvy.adapter.WallPaperAdapter
import com.android.statussavvy.base.BaseActivity
import com.android.statussavvy.base.BaseRvAdapter
import com.android.statussavvy.data.ScreensData
import com.android.statussavvy.databinding.HomeBinding
import com.android.statussavvy.insta.*
import com.android.statussavvy.network.ApiClient
import com.android.statussavvy.network.ApiRequest
import com.android.statussavvy.network.NetworkHelper
import com.android.statussavvy.utils.Constent
import com.android.statussavvy.utils.DataSet
import com.android.statussavvy.utils.FileManager
import com.android.statussavvy.utils.MyPreference
import com.ashudevs.facebookurlextractor.FacebookFile
import com.bumptech.glide.Glide
import com.downloader.*
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import io.fabric.sdk.android.services.common.AbstractSpiCall.DEFAULT_TIMEOUT
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import rx.Subscription
import java.io.File
import java.text.DecimalFormat


class Home : BaseActivity<HomeBinding>(), OnLogInListner {
    private val STORAGE_REQUEST_CODE: Int = 12
    private lateinit var progressDialog2: ProgressDialog
    lateinit var dataManager: DataManager
    var subscription: Subscription? = null
    var instagramStatusList = java.util.ArrayList<InstaStatusDetails>()

    companion object {
        var whatsApplist = mutableListOf<String>()
        var wallpaperlist = listOf<ScreensData.Wallpaper>()
        fun readableFileSize(j: Long): String {
            if (j <= 0) {
                return "0"
            }
            val d = j.toDouble()
            val log10 = (Math.log10(d) / Math.log10(1024.0)).toInt()
            return DecimalFormat("#,##0.#").format(
                d / Math.pow(
                    1024.0, log10.toDouble()
                )
            ) + " " + arrayOf("B", "kB", "MB", "GB", "TB")[log10]
        }

    }

    override fun setLayoutId(): Int {
        return R.layout.home
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun initM() {
        setTb()
        listener()
        setWhatsAppStatus()
        setInstagramStatus()
        setFacebookDownloader()
        setTrending()
        setHeader()
        // goActivity(DownloadActivity())
        //  throw RuntimeException("Test Crash") // Force a crash

    }


    private fun setHeader() {
        val userName: String = MyPreference.getUserName(this).trim()
        val userEmail: String = MyPreference.getUserEmail(this).trim()
        val userImage: String = MyPreference.getUserImage(this).trim()
        val mHeaderView: View = binding.navigation.getHeaderView(0)
        val userIv = mHeaderView.findViewById<ImageView>(R.id.userIv)
        val titleTv = mHeaderView.findViewById<TextView>(R.id.nameTv)
        val subTitleTv = mHeaderView.findViewById<TextView>(R.id.emailTv)

        if (userImage.isNotBlank()) {
            Glide.with(this@Home).load(userImage).into(userIv)
        }
        titleTv.text = userName
        subTitleTv.text = userEmail
    }

    private fun setTrending() {

        val rv = binding.main.trendRv
        val isNetwork = NetworkHelper.isNetworkAvailable(this)
        if (isNetwork) {
            val call = ApiClient.getApi().getScreens(ApiRequest.createRequest(this))
            call.enqueue(object : Callback<ScreensData> {
                override fun onResponse(call: Call<ScreensData>, response: Response<ScreensData>) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            wallpaperlist = data.wallpaperList
                            val adapter = WallPaperAdapter(this@Home, wallpaperlist)
                            rv.adapter = adapter
                            rv.layoutManager =
                                LinearLayoutManager(this@Home, RecyclerView.HORIZONTAL, false)
                            adapter.setOnItemClickListener(object :
                                BaseRvAdapter.OnItemClickListener {
                                override fun onItemClick(v: View?, position: Int) {
                                    val b = Bundle()
                                    b.putByte(DataSet.Type.TYPE, DataSet.Type.WALLPAPERS)
                                    b.putInt(DataSet.POSITION, position)
                                    goActivity(WallpaperView(), b)
                                }
                            })
                        }
                    }
                }

                override fun onFailure(call: Call<ScreensData>, t: Throwable) {
                }

            })
        } else Toast.makeText(this, "Network Not Available", Toast.LENGTH_SHORT).show()

        /* wallpaperlist = listOf<String>(
             "https://images.pexels.com/photos/4238351/pexels-photo-4238351.jpeg?auto=compress&cs=tinysrgb&w=600&lazy=load",
             "https://images.pexels.com/photos/4238351/pexels-photo-4238351.jpeg?auto=compress&cs=tinysrgb&w=600&lazy=load",
             "https://images.pexels.com/photos/4238351/pexels-photo-4238351.jpeg?auto=compress&cs=tinysrgb&w=600&lazy=load",
             "https://images.pexels.com/photos/4238351/pexels-photo-4238351.jpeg?auto=compress&cs=tinysrgb&w=600&lazy=load",
             "https://images.pexels.com/photos/4238351/pexels-photo-4238351.jpeg?auto=compress&cs=tinysrgb&w=600&lazy=load",
         ) as MutableList<String>*/

    }

    private fun setFacebookDownloader() {
        val videoUrlEt = binding.main.videourl
        val downloadBt = binding.main.downloadBt
        binding.main.clearUrl.setOnClickListener {
            videoUrlEt.text.clear()
        }
        downloadBt.setOnClickListener {
            val url = videoUrlEt.text.toString()
            // val url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            // val url = "https://images.pexels.com/photos/11802320/pexels-photo-11802320.jpeg"
            if (url.isNotBlank()) {

                // goActivity(Web())
                // downloadFile(facebookFile)
                //  fileDownload(url)
                if (url.contains(".mp4")) {

                    startDownloading(url, true)
                } else {
                    startDownloading(url, false)
                }

                /* @SuppressLint("StaticFieldLeak") object : FacebookExtractor(this, url, false) {
                     override fun onExtractionComplete(facebookFile: FacebookFile?) {
                         *//*
                        Log.e("TAG", "---------------------------------------");
                        Log.e("TAG", "facebookFile AutherName :: " + facebookFile.getAuthor());
                        Log.e("TAG", "facebookFile FileName :: " + facebookFile.getFilename());
                        Log.e("TAG", "facebookFile Ext :: " + facebookFile.getExt());
                        Log.e("TAG", "facebookFile SD :: " + facebookFile.getSdUrl());
                        Log.e("TAG", "facebookFile HD :: " + facebookFile.getHdUrl());
                        Log.e("TAG", "---------------------------------------");
                           *//*
                        //showMsg(facebookFile.toString())

                        //  downloadFile(facebookFile)

                    }

                    override fun onExtractionFail(error: Exception?) {
                        logD("Error : ${error!!.message}")
                        //  showMsg("Error :${error.message}")

                    }
                }*/
            } else {
                showMsg("Paste Url")
            }

        }
        // if (url.isNotBlank())
    }

    private fun fileDownload(url: String) {
        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(url))
        val title = URLUtil.guessFileName(url, null, null)
        request.setTitle(title)
        request.setDescription("Downloading file please wait...")
        val cookie = android.webkit.CookieManager.getInstance().getCookie(url)
        request.addRequestHeader("cookie", cookie)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)
        val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
        showMsg("Download Started.")
    }

    fun startDownloading(url: String, videoStatus: Boolean) {
        val con = binding.main.downloadingFramelayout
        val clearBtn = binding.main.clearDwn

        //val search_con = binding.main.searchConstraintLayout

        con.visibility = View.VISIBLE


        //  val filename = if (videoStatus) "${System.currentTimeMillis()}.mp4" else "${System.currentTimeMillis()}.jpg"
        val filename = URLUtil.guessFileName(url, null, null)

        val desiDir = Constent.dwnSavePath

        val config = PRDownloaderConfig.newBuilder()
            .setDatabaseEnabled(true)
            .build()
        PRDownloader.initialize(
            this@Home,
            PRDownloaderConfig.newBuilder().setDatabaseEnabled(false)
                // .setReadTimeout(AbstractSpiCall.DEFAULT_TIMEOUT)
                //  .setConnectTimeout(AbstractSpiCall.DEFAULT_TIMEOUT)
                .build()
        )

        val downloadId = PRDownloader.download(url, desiDir, filename).build()
            .setOnStartOrResumeListener(object : OnStartOrResumeListener {
                override fun onStartOrResume() {
                }

            })
            .setOnProgressListener { progress ->
                val j = progress!!.currentBytes * 100 / progress.totalBytes
                val sb = StringBuilder()
                sb.append("")
                val i = j.toInt()
                sb.append(i)
                sb.append("%")
                val sb2 = sb.toString()
                val process =
                    Home.readableFileSize(progress.currentBytes) + "/" + Home.readableFileSize(
                        progress.totalBytes
                    )

                //this@WallpaperView.showMsg(j.toString())
                if (progress.totalBytes >= 0) {
                    //  binding.save.isClickable = false
                    binding.main.downloadPercentage.text = sb2
                    binding.main.downloadProgresBar.progress = i
                    binding.main.downloadProgresBar.isIndeterminate = false
                }

            }
            .setOnCancelListener {
                con.visibility = View.GONE
            }

        downloadId.start(object : OnDownloadListener {
            override fun onDownloadComplete() {

                showMsg("Download Completed")
                con.visibility = View.GONE
                goActivity(SavedView())
                /*   val file = File()

                   val fromFile = Uri.fromFile(file)
                   val intent = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
                   intent.data = fromFile*/
                // binding.save.isClickable = true
            }

            override fun onError(error: Error?) {
                /*clearBtn.visibility = View.VISIBLE
                clearBtn.setOnClickListener {
                    con.visibility = View.GONE

                }*/
                showMsg("Invalid Url")
                con.visibility = View.GONE
                logD("error :" + error)

            }


        })

        clearBtn.setOnClickListener {
            downloadId.cancel()
        }

    }

    private fun downloadFile(fbFile: FacebookFile?) {
        binding.main.fbCon.visibility = View.VISIBLE
        binding.main.downloadingFramelayout.visibility = View.VISIBLE
        val fileName: String = fbFile!!.sdUrl.substring(fbFile.sdUrl.lastIndexOf('/') + 1)
        val shortFileName = fileName.substring(0, fileName.indexOf('?'))
        val path = "$shortFileName.mp4"
        val file: File
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                getString(R.string.app_name)
            )
        } else {
            file = File(Environment.getExternalStorageDirectory(), getString(R.string.app_name))
        }
        if (!file.exists()) {
            file.mkdirs()
        }
        PRDownloader.initialize(
            this@Home,
            PRDownloaderConfig.newBuilder().setDatabaseEnabled(false)
                .setReadTimeout(DEFAULT_TIMEOUT)
                .setConnectTimeout(DEFAULT_TIMEOUT).build()
        )
        PRDownloader.download(fbFile.sdUrl, file.absolutePath, path).build()
            .setOnProgressListener(object : OnProgressListener {
                override fun onProgress(progress: Progress?) {
                    val j = progress!!.currentBytes * 100 / progress.totalBytes
                    val sb = StringBuilder()
                    sb.append("")
                    val i = j.toInt()
                    sb.append(i)
                    sb.append("%")
                    val sb2 = sb.toString()
                    "${readableFileSize(progress.currentBytes)}/${readableFileSize(progress.totalBytes)}".also {
                        binding.main.textViewProgress.text = it
                    }
                    if (progress.totalBytes >= 0) {
                        binding.main.downloadPercentage.text = sb2
                        binding.main.downloadProgresBar.progress = i
                        binding.main.downloadProgresBar.isIndeterminate = false
                    }

                }
            }).start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    var file = File(
                        Environment.getExternalStorageDirectory(),
                        getString(R.string.app_name)
                    )
                    file = File(file.absoluteFile.toString() + File.separator + path)
                    val query = contentResolver.query(
                        FileProvider.getUriForFile(
                            this@Home,
                            applicationContext.packageName + ".provider",
                            file
                        ),
                        null as Array<String?>?,
                        null as String?,
                        null as Array<String?>?,
                        null as String?
                    )

                    val columnIndex = query!!.getColumnIndex("_display_name")
                    val columnIndex2 = query.getColumnIndex("_size")
                    binding.main.fbCon.visibility = View.VISIBLE
                    binding.main.downloadingFramelayout.visibility = View.GONE
                    query.moveToFirst()
                    val fromFile = Uri.fromFile(file)
                    val intent = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
                    intent.data = fromFile
                    sendBroadcast(intent)
                    showMsg("Download complete.You can get it in download section.")
                    val notificationHelper = NotificationHelper(this@Home)
                    val replace = query.getString(columnIndex).replace("\\d".toRegex(), "")
                        .replace(".mp", ".mp4")
                    notificationHelper.createNotification(
                        replace, "Download Complete : " + readableFileSize(
                            query.getLong(columnIndex2)
                        )
                    )
                    query.close()
                }

                override fun onError(error: Error?) {
                    Log.e("TAG", "onError: $error")
                }

            })

    }

    private fun setInstagramStatus() {
        dataManager = DataManager()
        if (dataManager != null && InstaStoryApplication.getInstance().cookieManager.cookie != null) {
            //  dataManager.setHeaders(Headers.Client.add(false, "Cookie", InstaStoryApplication.getInstance().cookieManager.cookie!!))
            getInstagramStatus()
        }
        ShowInstagramStatus()
        binding.main.instagramLogInButton.setOnClickListener {
            //insta.authorize()
            //showMsg("done")
            // Stories.login(this)
            val loginDialog = LoginDialog(this@Home, this@Home)
            //loginDialog = loginDialog
            loginDialog.showLoginDialog("https://www.instagram.com/")

        }

        /* Handler().postDelayed({
             //Stories.users(this)

         }, 100)*/
        //  Stories.getStories(this, "6164215428")
        /*
        Stories.storyList.observe(this,{
            for (item in it) {
                logD("name :${item.imageversions2.candidates}")
                showMsg(item.imageversions2.candidates.toString())
            }
        })
        Stories.list.observe(this,{
            for (item in it){
                logD("name :${item.user.fullname}")
            }
        })*/

    }

    private fun ShowInstagramStatus() {
        if (InstaStoryApplication.getInstance().cookieManager.isValid) {
            //instagramLogInView()
            //getInstagramStatus()
            getInstagramStatus()
            return
        }
    }

    private fun listener() {
        binding.main.enableBt.setOnClickListener {
            chekStoragePermissionAndShowWhatsappStory()
        }
        binding.main.download.setOnClickListener {
            goActivity(SavedView())
        }
        binding.main.wViewAll.setOnClickListener {
            val b = Bundle()
            b.putByte(DataSet.Type.TYPE, DataSet.Type.DIRECT_STATUS)
            goActivity(AllView(), b)
        }
        binding.main.wallpaperViewAll.setOnClickListener {
            val b = Bundle()
            b.putByte(DataSet.Type.TYPE, DataSet.Type.WALLPAPERS)
            goActivity(AllView(), b)
        }

    }

    fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.v(TAG, "Permission is granted")
                true
            } else {
                Log.v(TAG, "Permission is revoked")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted")
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setWhatsAppStatus() {
        /*  var isInstalled:Boolean
          if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
             isInstalled =FileManager.appInstalledOrNot( "com.whatsapp",packageManager)

          }else{
            isInstalled=false
          }*/


        if (FileManager.appInstalledOrNot("com.whatsapp", packageManager)) {
            // showMsg("check per :"+checkPermission())

            if (checkPermission() && MyPreference.isGranted(this)) {
                getWaStatus()
                // showMsg("granted")
            } else {
                binding.main.statusCard.visibility = View.GONE
                binding.main.perCon.visibility = View.VISIBLE
            }
        } else {
            showMsg("WhatsApp is not installed")
        }
    }


    private fun checkPermission(): Boolean {
        /*  return if (SDK_INT >= Build.VERSION_CODES.R) {
              Environment.isExternalStorageManager()
          } else {
              val result = ContextCompat.checkSelfPermission(this@Home, READ_EXTERNAL_STORAGE)
              val result1 = ContextCompat.checkSelfPermission(this@Home, WRITE_EXTERNAL_STORAGE)
              result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
          }*/
        val result = ContextCompat.checkSelfPermission(this@Home, WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun chekStoragePermissionAndShowWhatsappStory() {
//

        Dexter.withContext(this).withPermission("android.permission.WRITE_EXTERNAL_STORAGE")
            .withListener(object : PermissionListener {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                    //  initializeWhatsappRecycleView()
                    getWaStatus()
                }

                override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {
                    if (!permissionDeniedResponse.isPermanentlyDenied) {
                        Toast.makeText(
                            this@Home,
                            "Temporory Permission Rejected ",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@Home,
                            "Permenantly Permission Rejected ",
                            Toast.LENGTH_LONG
                        ).show()
                        requestPermissionInSetting()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissionRequest: PermissionRequest,
                    permissionToken: PermissionToken
                ) {
                    permissionToken.continuePermissionRequest()
                }
            }).check()

    }

    fun requestPermissionInSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val packageName = this.packageName ?: run {
            this?.packageName
        }
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        this?.apply {
            startActivityForResult(intent, STORAGE_REQUEST_CODE)
        } ?: run {
            this?.startActivityForResult(intent, STORAGE_REQUEST_CODE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getWaStatus() {
        binding.main.statusCard.visibility = View.VISIBLE
        binding.main.perCon.visibility = View.GONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (MyPreference.isGranted(this)) {
                getFiles()
            } else {
                //  openDirectory()
                getFolderPermission()

            }
        } else {

            try {
                //get status in <android 10

                if (Build.VERSION.SDK_INT < 30) {
                    // Less then 11
                    val targetPath = "/WhatsApp/Media/.Statuses"
                    whatsApplist.clear()
                    val listFiles = File(
                        Environment.getExternalStorageDirectory().toString() + targetPath
                    ).listFiles()
                    for (file in listFiles!!) {
                        if (file!!.endsWith(".jpg")) {
                            whatsApplist.add(file.toUri().toString())
                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getFolderPermission() {
        val storageManager = application.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val intent = storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()
        val separator = "%2F"
       // val targetDir = "Android${separator}media${separator}com.whatsapp${separator}WhatsApp${separator}Media${separator}.Statuses"
        val targetDir = "Android${separator}media${separator}com.whatsapp${separator}WhatsApp${separator}"
        var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI") as Uri
        var schema = uri.toString()
        schema = schema.replace("/root/", "/document/")
        schema += "%3A$targetDir"
        uri = Uri.parse(schema)
        intent.putExtra("android.provider.extra.INITIAL_URI", uri)
        intent.putExtra("android.content.extra.SHOW_ADVANCED", true)
        startActivityForResult(intent, 123)
        Handler().postDelayed({
            startActivity(Intent(this, OverHome::class.java))
        }, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 123) {
            val treeUri = data?.data
            if (treeUri != null) {
                contentResolver.takePersistableUriPermission(
                    treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                val fileDoc = DocumentFile.fromTreeUri(applicationContext, treeUri)
                MyPreference.saveUri(this, treeUri)
                logD("path uri :${treeUri.toString()}")
                val uriStr =
                    "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp"
                if (!treeUri.toString().matches(Regex(uriStr))) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        getFolderPermission()
                    }
                } else {
                    for (file: DocumentFile in fileDoc!!.listFiles()) {
                        if (file.name!!.matches(Regex("Media"))) {
                            for (it in file.listFiles()) {
                                if (it.name!!.matches(Regex(".Statuses"))) {
                                    MyPreference.setGrant(this)
                                    for (tt in it.listFiles()) {
                                        if (!tt.name!!.endsWith(".nomedia")) {
                                            logD("path :" + tt.uri.path.toString())
                                            whatsApplist.add(tt.uri.toString())
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (resultCode == STORAGE_REQUEST_CODE) {
                    showMsg("permission done")
                }

                setRecycler()
            }
        }

        /*if (resultCode == RESULT_OK && requestCode == 6) {
            if (data != null) {
                val uri = data.data
                //  if (uri.getPath().endsWith(".Statuses")) {
                Log.d("TAG", "onActivityResult: " + uri!!.path)
                val takeFlags = (data.flags
                        and Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val wa_status_uri =
                    Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    contentResolver.takePersistableUriPermission(
                        data.data!!,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
                MyPreference.setGrant(this)
                MyPreference.saveUri(this, wa_status_uri)
                getFiles()

                *//*} else {
                    // dialog when user gave wrong path
                    //showWrongPathDialog();
                }*//*
            }
        }*/
    }

    private fun getFiles() {
        val uriPath = MyPreference.uriString(this)
        if (uriPath != null) {
            logD("path :$uriPath")
            whatsApplist.clear()
            val fileDoc = DocumentFile.fromTreeUri(applicationContext, Uri.parse(uriPath))
            for (file: DocumentFile in fileDoc!!.listFiles()) {
                if (file.name!!.matches(Regex("Media"))){
                    for (it in file.listFiles()){
                        if (it.name!!.matches(Regex(".Statuses"))){
                            for (tt in it.listFiles()){
                                if (!tt.name!!.endsWith(".nomedia")){
                                    MyPreference.setGrant(this)
                                    logD("path :" + tt.uri.path.toString())
                                    whatsApplist.add(tt.uri.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
        /*if (Build.VERSION.SDK_INT >= 29) {
            logD("path :${uriMain.path}")

            val contentResolver = contentResolver
            val uri: Uri = uriMain
            val buildChildDocumentsUriUsingTree = DocumentsContract.buildChildDocumentsUriUsingTree(uri, DocumentsContract.getDocumentId(uri))
            var cursor: Cursor? = null
            try {
                cursor = contentResolver.query(
                    buildChildDocumentsUriUsingTree,
                    arrayOf("document_id"),
                    null as String?,
                    null as Array<String?>?,
                    null as String?
                )
                while (cursor!!.moveToNext()) {
                    val uriStr=DocumentsContract.buildDocumentUriUsingTree(uri,cursor.getString(0))
                    whatsApplist!!.add(File(uriStr.path))
                    logD("path :${uriStr.path}")

                }
                runOnUiThread {
                    //here set your adapter in list and set data from statusObjectsList
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } catch (th: Throwable) {
                throw th
            }

        }*/
        setRecycler()
    }

    private fun setRecycler() {
        val rv = binding.main.wStatusRv
        val adapter = StatusAdapter(this, whatsApplist)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        adapter.setOnItemClickListener(object : BaseRvAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                val b = Bundle()
                b.putInt(DataSet.POSITION, position)
                b.putByte(DataSet.Type.TYPE, DataSet.Type.DIRECT_STATUS)
                goActivity(MediaView(), b)
            }

        })
    }

    fun openDirectory() {
        val path =
            Environment.getExternalStorageDirectory()
                .toString() + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
        val file = File(path)
        var startDir = ""
        var secondDir: String
        val finalDirPath: String
        if (file.exists()) {
            // startDir = "Android%2Fdata%2Fcom.pubg.krmobile%2Fthefilder%2Fsubfolder%2Fdeepersubfolder";
            // "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
            val sap = "%2F"
            startDir =
                "Android" + sap + "media" + sap + "com.whatsapp" + sap + "WhatsApp" + sap + "Media" + sap + ".Statuses"
        }
        val sm = getSystemService(STORAGE_SERVICE) as StorageManager
        var intent: Intent? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                intent = sm.primaryStorageVolume.createOpenDocumentTreeIntent()
            }
        }
        var uri = intent!!.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
        var scheme = uri.toString()
        Log.d("TAG", "INITIAL_URI scheme: $scheme")
        scheme = scheme.replace("/root/", "/document/")
        finalDirPath = "$scheme%3A$startDir"
        uri = Uri.parse(finalDirPath)
        intent.putExtra("android.provider.extra.INITIAL_URI", uri)
        Log.d("TAG", "uri: $uri")
        try {
            startActivityForResult(intent, 6)
        } catch (ignored: ActivityNotFoundException) {
        }
    }

    private fun setTb() {
        val toolbar = binding.main.toolbar
        val drawerLayout = binding.drawerLayout
        setSupportActionBar(toolbar)
        val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        //  drawerLayout.setDrawerListener(actionBarDrawerToggle)
        //  actionBarDrawerToggle.syncState()
        //  val drawable =getDrawable(R.drawable.menu_icon)
        //  actionBarDrawerToggle.isDrawerIndicatorEnabled=false
        // actionBarDrawerToggle.setHomeAsUpIndicator(drawable)
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //  supportActionBar?.setHomeAsUpIndicator(R.drawable.menu_icon)

        val nav = binding.navigation
        nav.setNavigationItemSelectedListener(object : OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.download_item -> {
                        drawerLayout.closeDrawers()
                        goActivity(SavedView())
                    }
                    R.id.invite_item -> {
                        drawerLayout.closeDrawers()
                        goActivity(Invite())
                    }

                    R.id.nav_privacyPolicy -> {
                        drawerLayout.closeDrawers()
                        setPrivacy(
                            getString(R.string.privacy_policy),
                            "https://statussavvy.app/privacy-policy.html"
                        )
                    }
                    R.id.nav_contactUs -> {
                        contactUs()
                    }
                    R.id.nav_telegram -> {
                        drawerLayout.closeDrawers()
                        followUs(getString(R.string.telegram_link))
                    }
                    R.id.nav_instagram -> {
                        drawerLayout.closeDrawers()
                        followUs(getString(R.string.instagram_link))
                    }
                    R.id.nav_twitter -> {
                        drawerLayout.closeDrawers()
                        followUs(getString(R.string.twitter_link))
                    }
                    R.id.nav_facebook -> {
                        drawerLayout.closeDrawers()
                        followUs(getString(R.string.facebook_link))
                    }
                    R.id.nav_youtube -> {
                        drawerLayout.closeDrawers()
                        followUs(getString(R.string.youtube_link))
                    }

                }
                return true
            }

        })
        binding.main.menu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun contactUs() {
        try {


            val intent = Intent(Intent.ACTION_SENDTO)
            val email = "apphelpmg@gmail.com"
            intent.data = Uri.parse("mailto:") // only email apps should handle this
            val userId = MyPreference.getUserId(this@Home)
            val versionName = BuildConfig.VERSION_NAME

            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.app_name) + ":Feedback/Support"
            )
            //intent.putExtra(Intent.Extra_Si, getString(R.string.app_name) + ":Feedback/Support");
            //intent.putExtra(Intent.Extra_Si, getString(R.string.app_name) + ":Feedback/Support");
            intent.putExtra(
                Intent.EXTRA_TEXT,
                " \n\n\n" + "User Id : " + userId + "\n" + "App Version : " + versionName + "\n" + "Device : " + Build.MODEL
            )
            // intent.putExtra(Intent.EXTRA_TEXT, feedback_et.getText());
            // intent.putExtra(Intent.EXTRA_TEXT, feedback_et.getText());
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showMsg("App not Found")
        }
    }

    open fun followUs(urlLink: String?) {
        this.startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(urlLink)))
    }

    private fun setPrivacy(title: String, url: String) {
        val dialog = Dialog(this)
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.privacy_dialog)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        val title_tv = dialog.findViewById<TextView>(R.id.p_dialog_title)
        val webView = dialog.findViewById<WebView>(R.id.policy_wev)
        val accept_bt = dialog.findViewById<TextView>(R.id.done_bt)
        webView.loadUrl(url)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
        title_tv.text = title
        accept_bt.setOnClickListener { v: View? -> dialog.dismiss() }
        if (!(this as Activity).isFinishing) {
            //show dialog
            dialog.show()
        }
    }


    override fun onLogIn(str: String?) {
        if (str == OnLogInListner.Instagram) {
            //  instagramLogInView()
            showMsg("logged")
            getInstagramStatus()
        }

    }

    private fun getInstagramStatus() {
        binding.main.instaLoginCon.visibility = View.GONE
        binding.main.instaStoryCard.visibility = View.VISIBLE
        if (dataManager != null) {
            dataManager.setHeaders(
                Headers.Client.add(
                    false,
                    "Cookie",
                    InstaStoryApplication.getInstance().cookieManager.cookie
                )
            )
        }
        // val subscription2: Subscription = subscription!!
        if (subscription != null) {
            subscription!!.unsubscribe()
        } else {

            //   binding.main.instaLoginCon.visibility = View.VISIBLE
            //    binding.main.instaStoryCard.visibility = View.GONE

        }

        if (dataManager != null) {
            try {

                dataManager.reelsTray.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Observer<Response<StoriesTrayResponse?>> {
                        override fun onSubscribe(d: Disposable) {
                            Log.d("TAG", "In onSubscribe()$d")
                        }

                        override fun onNext(storiesTrayResponseResponse: Response<StoriesTrayResponse?>) {
                            val stories = storiesTrayResponseResponse.body()
                            val stList: ArrayList<InstaStatusDetails> =
                                ArrayList<InstaStatusDetails>()
                            if (stories != null) {
                                //showMsg(stories.toString())
                                // instaProgressBar.setVisibility(View.GONE)

                                instagramStatusList.clear()
                                Log.e(TAG, "onNext: " + stories.tray)
                                if (stories.tray != null) for (i in 0 until stories.tray.size) {
                                    if (stories.tray[i].user != null) {
                                        instagramStatusList.add(InstaStatusDetails(stories.tray[i]))
                                    }
                                }
                                initializeInstagramRecycleView(instagramStatusList)
                            }
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                            // instaProgressBar.setVisibility(View.GONE)
                            Log.d("TAG", "In onError()")
                        }

                        override fun onComplete() {
                            Log.d("TAG", "In onCompleted()")
                        }
                    })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }

    private fun initializeInstagramRecycleView(instagramStatusList: List<InstaStatusDetails>) {
        for (i in instagramStatusList.indices) {
            /*  if (i % 2 == 0) {
                  instagramStatusList.removeAt(i)
              }*/
        }
        if (instagramStatusList != null && this.instagramStatusList.size > 0) {
            val rv = binding.main.instaRv
            binding.main.instaLoginCon.visibility = View.GONE
            binding.main.instaStoryCard.visibility = View.VISIBLE
            //logD(instagramStatusList[0].tray.user.fullname!!)
            //showMsg(instagramStatusList[0].tray.user.fullname!!)
            val adapter = InstaStoryAdapter(this, instagramStatusList)
            rv.adapter = adapter
            rv.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
            adapter.setOnItemClickListener(object : BaseRvAdapter.OnItemClickListener {
                override fun onItemClick(v: View?, position: Int) {
                    val data = instagramStatusList[position]

                    progressDialog2 = ProgressDialog(this@Home, 4)
                    progressDialog2.setCancelable(false)
                    progressDialog2.setTitle("Please Wait")
                    progressDialog2.setMessage("Getting Status")
                    progressDialog2.show()
                    if (data.statusHolder == null) {
                        loadMedia(position)
                        return
                    }
                    val intent = Intent(this@Home, StatusActivity::class.java)
                    intent.putExtra(
                        DataSet.Instagram.STATUS,
                        instagramStatusList[position].statusHolder
                    )

                    intent.putExtra(DataSet.FROM, DataSet.INSTA_TAG)
                    logD(instagramStatusList[position].tray.user.username)
                    // showMsg(instagramStatusList[position].statusHolder.statusList[0].userName)
                    startActivity(intent)
                    progressDialog2.dismiss()

                }

            })
        }

    }

    private fun loadMedia(i: Int) {
        val user: StoriesTrayResponse.Tray.User = instagramStatusList.get(i).getTray().getUser()
        dataManager.setHeaders(
            Headers.Client.add(
                false,
                "Cookie",
                InstaStoryApplication.getInstance().cookieManager.cookie
            )
        )
//        getReelMediaObservable(user.getPk()).subscribe(new do_subscribe(user, i, this.instaStatusList.get(i)));
        //        getReelMediaObservable(user.getPk()).subscribe(new do_subscribe(user, i, this.instaStatusList.get(i)));
        this.dataManager.getReelMedia(user.pk).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(
                Schedulers.io()
            ).subscribe(object :
                Observer<Response<StoriesMediaResponse?>?> {
                override fun onSubscribe(d: Disposable) {
                    Log.d("TAG", "In onSubscribe()$d")
                }

                override fun onNext(storiesMediaResponseResponse: Response<StoriesMediaResponse?>?) {
//                List<StoriesMediaResponse.Item> items = storiesMediaResponseResponse.body().getItems();
//                if (items != null) {
//                    ArrayList arrayList = new ArrayList();
//                    arrayList.clear();
//                    for (int i = 0; i < items.size(); i++) {
//                        if (items.get(i).getMediaType() == 2) {
//                            arrayList.add(new StatusList(true, items.get(i).getVideoVersions().get(0).getUrl(), user.getProfilePicUrl(), user.getUsername(), user.getFullname()));
//                        } else {
//                            arrayList.add(new StatusList(false, items.get(i).getImageVersions2().getCandidates().get(0).getUrl(), user.getProfilePicUrl(), user.getUsername(), user.getFullname()));
//                        }
//                    }
//
//
//                    StatusHolder statusHolder = new StatusHolder();
//                    statusHolder.setStatusList(arrayList);
//                    tray.setStatusHolder(statusHolder);
//                    if (progressDialog.isShowing()) {
//                        progressDialog.dismiss();
//                        Intent intent = new Intent(context, StatusActivity.class);
//                        intent.putExtra("parceble", statusHolder);
//                        intent.putExtra("from", MainActivity.TAG_INSTA);
//                        context.startActivity(intent);
//                    }
//                }
                    onStatusResponse(
                        storiesMediaResponseResponse,
                        user,
                        instagramStatusList[i]
                    )
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    Log.d("TAG", "In onError()")
                }

                override fun onComplete() {
                    Log.d("TAG", "In onCompleted()")
                }
            })
    }


    override fun onLogInFailed() {

    }

    //    public int getItemCount() {
    //        Log.e("TAG", "getItemCount: "+instaStatusList.size() );
    //        return instaStatusList.size();
    //    }
    //
    //    static class MyViewHolder extends RecyclerView.ViewHolder {
    //        ConstraintLayout constraintLayout;
    //        CircleImageView instaProfilePic = ((CircleImageView) this.itemView.findViewById(R.id.instaProfilePic));
    //        TextView instaUserName = ((TextView) this.itemView.findViewById(R.id.instaUserName));
    //
    //        MyViewHolder(View view) {
    //            super(view);
    //            this.constraintLayout = (ConstraintLayout) view.findViewById(R.id.instaConstraintLayout);
    //        }
    //    }
    //    class do_subscribe extends Subscriber<Response<StoriesMediaResponse>> {
    //        int position;
    //        private InstaStatusDetails tray;
    //        StoriesTrayResponse.Tray.User user;
    //
    //        public void onCompleted() {
    //        }
    //
    //        public void onError(Throwable th) {
    //        }
    //
    //        do_subscribe(StoriesTrayResponse.Tray.User user2, int i, InstaStatusDetails instaStatusDetails) {
    //            this.position = i;
    //            this.user = user2;
    //            this.tray = instaStatusDetails;
    //        }
    //
    //        public void onNext(Response<StoriesMediaResponse> response) {
    //            if (response.code() == 200) {
    //
    //            }
    //        }
    //    }
    fun onStatusResponse(
        response: Response<StoriesMediaResponse?>?,
        user: StoriesTrayResponse.Tray.User,
        instaStatusDetails: InstaStatusDetails
    ) {
        if (response!!.isSuccessful) {

            val items = response!!.body()!!.items
            if (items != null) {
                // val arrayList: ArrayList = java.util.ArrayList<Any?>()
                val arrayList: ArrayList<StatusList> = ArrayList()

                arrayList.clear()
                for (i in items.indices) {
                    if (items[i].mediaType === 2) {
                        arrayList.add(
                            StatusList(
                                true,
                                items[i].videoVersions[0].url,
                                user.profilePicUrl,
                                user.username,
                                user.fullname
                            )
                        )
                    } else {
                        arrayList.add(
                            StatusList(
                                false,
                                items[i].imageVersions2.candidates[0].url,
                                user.profilePicUrl,
                                user.username,
                                user.fullname
                            )
                        )
                    }
                }
                val statusHolder = StatusHolder()
                statusHolder.setStatusList(arrayList)
                instaStatusDetails.statusHolder = statusHolder
                if (progressDialog2.isShowing()) {
                    Log.e("TAG", "onStatusResponse: i m here ")
                    this.progressDialog2.dismiss()
                    val intent = Intent(this, StatusActivity::class.java)
                    intent.putExtra(DataSet.Instagram.STATUS, statusHolder)
                    intent.putExtra(DataSet.FROM, DataSet.INSTA_TAG)
                    startActivity(intent)
                }
            }


        }
    }


}




