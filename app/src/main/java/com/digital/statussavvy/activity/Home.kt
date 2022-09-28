package com.digital.statussavvy.activity

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.storage.StorageManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digital.statussavvy.R
import com.digital.statussavvy.adapter.StatusAdapter
import com.digital.statussavvy.databinding.HomeBinding
import com.digital.statussavvy.utils.MyPreference
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.moon.baselibrary.base.BaseActivity
import java.io.File


class Home : BaseActivity<HomeBinding>() {
    var whatsApplist = mutableListOf<String>()

    override fun setLayoutId(): Int {
        return R.layout.home
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun initM() {
        setTb()
        listener()
        setWhatsAppStatus()
        setInstagramStatus()
    }

    private fun setInstagramStatus() {

    }

    private fun listener() {
        binding.main.enableBt.setOnClickListener {
            chekStoragePermissionAndShowWhatsappStory()
        }
        binding.main.instagramLogInButton.setOnClickListener {

        }
    }

    fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Log.v(TAG, "Permission is granted")
                true
            } else {
                Log.v(TAG, "Permission is revoked")
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
        val targetDir =
            "Android${separator}media${separator}com.whatsapp${separator}WhatsApp${separator}Media${separator}.Statuses"
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
                contentResolver.takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

                val fileDoc = DocumentFile.fromTreeUri(applicationContext, treeUri)
                
                for (file: DocumentFile in fileDoc!!.listFiles()) {
                    if (!file.name!!.endsWith(".nomedia")) {
                        MyPreference.saveUri(this, treeUri)
                        MyPreference.setGrant(this)

                        logD("path :" + file.uri.path.toString())

                        whatsApplist.add(file.uri.toString())
                        // whatsApplist.add(uriStr)
                    }
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
            contentResolver.takePersistableUriPermission(
                Uri.parse(uriPath),
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            val fileDoc = DocumentFile.fromTreeUri(applicationContext, Uri.parse(uriPath))
            for (file: DocumentFile in fileDoc!!.listFiles()) {
                if (!file.name!!.endsWith(".nomedia")) {
                    whatsApplist.add(file.uri.toString())
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
//------------------------------------------------------------------------

        /*// uri is the path which we've saved in our shared pref
        val fromTreeUri = DocumentFile.fromTreeUri(this, uriMain)
        val documentFiles = fromTreeUri!!.listFiles()

        // Android 11
        val targetPath = "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
        val listFiles =
            File(Environment.getExternalStorageDirectory().toString() + targetPath).listFiles()
        val path=fromTreeUri.uri.path+"/com.Whatsapp/WhatsApp/Media/.Statuses"
        val files=File(path).listFiles()
        logD("path :${fromTreeUri!!.uri.path}")

        for (it in listFiles) {
            if (it.isFile){
                logD("path :${it.path}")

              *//*  if (it.path.endsWith(".jpg")){
                  logD("path :${it.name}")
                  whatsApplist.add(it)
              }*//*
          }
        }*/
        setRecycler()
    }

    private fun setRecycler() {
        val rv = binding.main.wStatusRv
        val adapter = StatusAdapter(this, whatsApplist)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
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
        //val drawerLayout=binding.drawerLayout
        setSupportActionBar(toolbar)
        //val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        // actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    /*else {

        // Android 11
        val targetPath = "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
        val listFiles = File(
            Environment.getExternalStorageDirectory().toString() + targetPath
        ).listFiles()
        for (file in listFiles) {
            if (file.endsWith(".jpg")) {
                whatsApplist?.add(file)
            }
        }
    }*/
}