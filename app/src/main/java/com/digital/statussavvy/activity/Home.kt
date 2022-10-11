package com.digital.statussavvy.activity

import android.Manifest
import android.annotation.SuppressLint
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
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ashudevs.facebookurlextractor.FacebookExtractor
import com.ashudevs.facebookurlextractor.FacebookFile
import com.digital.statussavvy.FB.DownloadActivity
import com.digital.statussavvy.NotificationHelper
import com.digital.statussavvy.R
import com.digital.statussavvy.adapter.InstaStoryAdapter
import com.digital.statussavvy.adapter.StatusAdapter
import com.digital.statussavvy.adapter.WallPaperAdapter
import com.digital.statussavvy.databinding.HomeBinding
import com.digital.statussavvy.insta.*
import com.digital.statussavvy.utils.DataSet
import com.digital.statussavvy.utils.MyPreference
import com.downloader.*
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.moon.baselibrary.base.BaseActivity
import com.moon.baselibrary.base.BaseRvAdapter
import io.fabric.sdk.android.services.common.AbstractSpiCall.DEFAULT_TIMEOUT
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Response
import rx.Subscription
import java.io.File
import java.text.DecimalFormat


class Home : BaseActivity<HomeBinding>(), OnLogInListner {
    private lateinit var progressDialog2: ProgressDialog
    var dataManager: DataManager = DataManager()
    var subscription: Subscription? = null
    var instagramStatusList = java.util.ArrayList<InstaStatusDetails>()

    companion object {
        var whatsApplist = mutableListOf<String>()
        fun readableFileSize(j: Long): String? {
            if (j <= 0) {
                return "0"
            }
            val d = j.toDouble()
            val log10 = (Math.log10(d) / Math.log10(1024.0)).toInt()
            return DecimalFormat("#,##0.#").format(
                d / Math.pow(
                    1024.0,
                    log10.toDouble()
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
        // goActivity(DownloadActivity())

    }

    private fun setTrending() {
        val rv = binding.main.trendRv
        val list = listOf<String>(
            "https://images.pexels.com/photos/4238351/pexels-photo-4238351.jpeg?auto=compress&cs=tinysrgb&w=600&lazy=load",
            "https://images.pexels.com/photos/4238351/pexels-photo-4238351.jpeg?auto=compress&cs=tinysrgb&w=600&lazy=load",
            "https://images.pexels.com/photos/4238351/pexels-photo-4238351.jpeg?auto=compress&cs=tinysrgb&w=600&lazy=load",
            "https://images.pexels.com/photos/4238351/pexels-photo-4238351.jpeg?auto=compress&cs=tinysrgb&w=600&lazy=load",
            "https://images.pexels.com/photos/4238351/pexels-photo-4238351.jpeg?auto=compress&cs=tinysrgb&w=600&lazy=load",
        )
        val adapter = WallPaperAdapter(this, list)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        adapter.setOnItemClickListener(object : BaseRvAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                val b = Bundle()
                b.putByte(DataSet.Type.TYPE, DataSet.Type.WALLPAPERS)
                goActivity(MediaView(), b)
            }
        })
    }

    private fun setFacebookDownloader() {
        val videoUrlEt = binding.main.videourl
        val downloadBt = binding.main.downloadBt
        downloadBt.setOnClickListener {
            val url = videoUrlEt.text.toString()
            if (url.isNotBlank()) {


                @SuppressLint("StaticFieldLeak")
                object : FacebookExtractor(this, url, false) {
                    override fun onExtractionComplete(facebookFile: FacebookFile?) {
                        /*
                        Log.e("TAG", "---------------------------------------");
                        Log.e("TAG", "facebookFile AutherName :: " + facebookFile.getAuthor());
                        Log.e("TAG", "facebookFile FileName :: " + facebookFile.getFilename());
                        Log.e("TAG", "facebookFile Ext :: " + facebookFile.getExt());
                        Log.e("TAG", "facebookFile SD :: " + facebookFile.getSdUrl());
                        Log.e("TAG", "facebookFile HD :: " + facebookFile.getHdUrl());
                        Log.e("TAG", "---------------------------------------");
                           */
                        downloadFile(facebookFile)


                    }

                    override fun onExtractionFail(error: Exception?) {
                        logD("Error : ${error!!.message}")
                    }

                }
            }

        }
        // if (url.isNotBlank())
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
                    binding.main.textViewProgress.text =
                        readableFileSize(progress.currentBytes) + "/" + readableFileSize(progress.totalBytes)
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
                    val columnIndex2 = query!!.getColumnIndex("_size")
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
                        replace,
                        "Download Complete : " + readableFileSize(
                            query.getLong(columnIndex2)
                        )
                    )

                }

                override fun onError(error: Error?) {
                    Log.e("TAG", "onError: $error")
                }

            })

    }

    private fun setInstagramStatus() {
        /* val insta = InstagramApp(
             Home@ this,
             DataSet.Instagram.CLIENT_ID,
             DataSet.Instagram.CLIENT_SECRET,
             DataSet.Instagram.CALLBACK_URL
         )*/

        /*  val listener = object : InstagramApp.OAuthAuthenticationListener {
              override fun onSuccess() {
                  logE("Userid :", insta.getId())
                  logE("Name :", insta.getName())
                  logE("UserName :", insta.getUserName())
              }

              override fun onFail(error: String?) {
                  showMsg(error + "")
              }


          }
          insta.setListener(listener)*/
        dataManager.setHeaders(
            Headers.Client.add(
                false,
                "Cookie",
                InstaStoryApplication.getInstance().cookieManager.cookie
            )
        )
        if (dataManager.mHeaders != null) {
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

        if (isStoragePermissionGranted() && MyPreference.isGranted(this)) {
            getWaStatus()

        } else {
            binding.main.statusCard.visibility = View.GONE
            binding.main.perCon.visibility = View.VISIBLE
        }
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

                    for (file in listFiles) {
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
                                    for (tt in it.listFiles()) {
                                        if (!tt.name!!.endsWith(".nomedia")) {
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

                //--------------------------------------------
                /* for (file: DocumentFile in fileDoc!!.listFiles()) {
                     if (!file.name!!.endsWith(".nomedia")) {
                         MyPreference.saveUri(this, treeUri)
                         MyPreference.setGrant(this)

                         logD("path :" + file.uri.path.toString())

                         whatsApplist.add(file.uri.toString())
                         // whatsApplist.add(uriStr)
                     }
                 }*/
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
        //val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        // actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val nav = binding.navigation
        nav.setNavigationItemSelectedListener(object : OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.download_item -> {
                        drawerLayout.closeDrawers()
                        goActivity(DownloadActivity())
                    }
                }
                return true
            }

        })

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
        if (dataManager.mHeaders != null) {
            dataManager.setHeaders(
                Headers.Client.add(
                    false,
                    "Cookie",
                    InstaStoryApplication.getInstance().getCookieManager().getCookie()
                )
            )
        }
        // val subscription2: Subscription = subscription!!
        if (subscription != null) {
            subscription!!.unsubscribe()
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




