package com.digital.statussavvy.activity

import android.content.*
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.viewpager2.widget.ViewPager2
import com.digital.statussavvy.R
import com.digital.statussavvy.adapter.MediaAdapter
import com.digital.statussavvy.databinding.MediaViewBinding
import com.digital.statussavvy.utils.Constent
import com.digital.statussavvy.utils.DataSet
import com.digital.statussavvy.utils.FileManager
import com.digital.statussavvy.utils.MyPreference
import com.moon.baselibrary.base.BaseActivity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

class MediaView : BaseActivity<MediaViewBinding>() {
    private var position_: Int? = null
    private lateinit var adapter: MediaAdapter
    private lateinit var uriPath: String
    public var list = mutableListOf<String>()
    override fun setLayoutId(): Int {
        return R.layout.media_view
    }

    override fun initM() {
        setWaStatus()
    }

    private fun setWaStatus() {
        val shareBt = binding.shareE
        val container = binding.con1
        val deleteBt = binding.deleteBt
        val type = intent.getByteExtra(DataSet.Type.TYPE, -1)
        val pos = intent.getIntExtra(DataSet.POSITION, -1)
        if (type == DataSet.Type.DIRECT_STATUS) {
            list = Home.whatsApplist
        } else if (type == DataSet.Type.SAVED_STATUS) {
            list = SavedView.savedWaList
            container.visibility = View.GONE
            shareBt.visibility = View.VISIBLE
            deleteBt.visibility = View.VISIBLE
        } else if (type == DataSet.Type.SAVED_WALLPAPERS) {
            container.visibility = View.GONE
            shareBt.visibility = View.VISIBLE
            list = SavedView.savedWallpaperList
            deleteBt.visibility = View.VISIBLE

        } else if (type == DataSet.Type.WALLPAPERS) {
           // list = Home.wallpaperlist
        } else if (type == DataSet.Type.SAVED_INSTA) {
            container.visibility = View.GONE
            shareBt.visibility = View.VISIBLE
            deleteBt.visibility = View.VISIBLE
            list = SavedView.instaStatusList
        }
        uriPath = list[pos]
        //val photo = binding.photo
        //val video = binding.video
        /* if (uriPath.endsWith(".mp4")) {
             photo.visibility = View.GONE
             video.setVideoURI(Uri.parse(uriPath))
             video.setOnPreparedListener { mp -> mp!!.start() }

         } else {
             video.visibility = View.GONE
             Glide.with(this).load(uriPath).diskCacheStrategy(DiskCacheStrategy.ALL).into(photo)

         }*/

        val vp = binding.vp
        adapter = MediaAdapter(this, list)
        vp.adapter = adapter
        vp.setCurrentItem(pos, false)
        position_ = pos
        vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                position_ = position
            }
        })

        binding.save.setOnClickListener {
            save(list[position_!!])
        }
        binding.share.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            uriPath = list[position_!!]
            val imagetUri = Uri.parse(uriPath)
            if (uriPath.endsWith(".mp4"))
                sharingIntent.type = "video/mp4"
            else sharingIntent.type = "image/jpeg"
            if (Build.VERSION.SDK_INT >= 24 && type != DataSet.Type.DIRECT_STATUS) {
                val photoURI = FileProvider.getUriForFile(
                    this,
                    this.applicationContext.packageName + ".provider",
                    File(imagetUri.path)
                )
                sharingIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
            } else {
                sharingIntent.putExtra(Intent.EXTRA_STREAM, imagetUri);
            }
            startActivity(Intent.createChooser(sharingIntent, "Share image using"))
        }
        shareBt.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            uriPath = list[position_!!]
            val imagetUri = Uri.parse(uriPath)
            if (uriPath.endsWith(".mp4"))
                sharingIntent.type = "video/mp4"
            else sharingIntent.type =
                if (type == DataSet.Type.SAVED_WALLPAPERS) "image/jpg" else "image/jpeg"
            if (Build.VERSION.SDK_INT >= 24 && type != DataSet.Type.DIRECT_STATUS) {
                val photoURI = FileProvider.getUriForFile(
                    this,
                    this.applicationContext.packageName + ".provider",
                    File(imagetUri.path)
                )
                sharingIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
            } else {
                sharingIntent.putExtra(Intent.EXTRA_STREAM, imagetUri);
            }
            startActivity(Intent.createChooser(sharingIntent, "Share image using"))
        }
        deleteBt.setOnClickListener {
            deleteFile_(list[position_!!])
        }
    }

    private fun deleteFile_(uri: String) {
        val uri_ = FileManager.getContentUriId(this, Uri.parse(uri))
        if (!uri.endsWith(".mp4")) {  //1.file uri->content uri
            try {
                deleteAPI28(uri_, this)

            } catch (e: Exception) {
                try {
                    deleteAPI30(uri_)
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        } else {
            val uri_v = Uri.parse(uri)
            val file = File(uri_.path)

            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString() + Constent.SavePath + getFileName(Uri.parse(uri))
            val isDelete = File(path).delete()
            if (isDelete) {
                showMsg("file deleted")
            }
        }

        if (list != null) {
            list.remove(list[position_!!])
            adapter.notifyItemRemoved(position_!!)
            //adapter.removeItem(position_!! )
            // adapter.notifyDataSetChanged()
            MyPreference.setChange(this, true)
        }

        if (list.size == 0) {
            finish()
        }

    }

    private fun deleteAPI30(uri_: Uri) {
        val contentResolver: ContentResolver = this.contentResolver
        // API 30

        // API 30
        val uriList: ArrayList<Uri> = ArrayList()
        Collections.addAll(uriList, uri_)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            MediaStore.createDeleteRequest(contentResolver, uriList)
        } else {
            TODO("VERSION.SDK_INT < R")
        }
        val senderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender)
            .setFillInIntent(null)
            .setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION, 0)
            .build()
        deleteResultLauncher.launch(senderRequest)
        /*   startIntentSenderForResult(
               pendingIntent.intentSender,
               DELETE_REQUEST_CODE, null, 0,
               0, 0, null
           )*/
    }

    fun deleteAPI28(uri: Uri?, context: Context): Int {
        val resolver = context.contentResolver
        val del = resolver.delete(uri!!, null, null)
        showMsg("deleted file")
        return del
    }

    private fun save(uri: String) {
        if (checkFileExists(uri)) {
            showMsg("Already saved")
        } else {
            saveFile_f(uri)
        }
    }

    private fun saveFile_f(uri: String) {
        if (uri.endsWith(".mp4")) {
            val inputStream = contentResolver.openInputStream(Uri.parse(uri))
            val filename = "${getFileName(Uri.parse(uri))}"
            try {
                val values = ContentValues()
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                values.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + Constent.SavePath
                )
                // val uri_ = contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
                val uri_ =
                    contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)

                val outputStream: OutputStream =
                    uri_?.let { contentResolver.openOutputStream(it) }!!
                if (inputStream != null) {
                    outputStream.write(inputStream.readBytes())
                }
                outputStream.close()
                //save video
                showMsg("saved video")
            } catch (e: Exception) {
                showMsg("Failed :$e")
                logD("Failed: " + e)
            }
        } else {
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(uri))
            val filename = "${getFileName(Uri.parse(uri))}"
            var fos: OutputStream? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentResolver.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH,
                            Environment.DIRECTORY_PICTURES + Constent.SavePath
                        )
                    }
                    val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    fos = imageUri?.let { resolver.openOutputStream(it) }
                }
            } else {
                val imageDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + Constent.SavePath)
                val image = File(imageDir, filename)
                fos = FileOutputStream(image)

            }
            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                showMsg("saved image")

            }

        }


    }


    private fun checkFileExists(uri: String): Boolean {
        var isExists = false
        val imageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + Constent.SavePath)

        val folder = File(imageDir.absolutePath)
        if (folder.exists()) {

            for (file in folder.listFiles()!!) {
                val uri_c = file.toUri()
                if (getFileName(Uri.parse(uri))!!.matches(Regex(getFileName(file.toUri())!!))) {
                    isExists = true
                    break
                }
            }
        }
        return isExists

    }

    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    // result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    val nameIndex: Int = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    result = cursor.getString(nameIndex)
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    var deleteResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult(),
            ActivityResultCallback<ActivityResult> { result ->
                if (result.getResultCode() === RESULT_OK) {
                    showMsg("delete file")
                }
            }
        )

}