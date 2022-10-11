package com.digital.statussavvy.activity

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.digital.statussavvy.R
import com.digital.statussavvy.adapter.MediaAdapter
import com.digital.statussavvy.databinding.WaStatusViewBinding
import com.digital.statussavvy.utils.Constent
import com.digital.statussavvy.utils.DataSet
import com.moon.baselibrary.base.BaseActivity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MediaView : BaseActivity<WaStatusViewBinding>() {
    private lateinit var uriPath: String
    public var list = mutableListOf<String>()
    override fun setLayoutId(): Int {
        return R.layout.wa_status_view
    }

    override fun initM() {
        setWaStatus()
    }

    private fun setWaStatus() {

        val type = intent.getByteExtra(DataSet.Type.TYPE, -1)
        val pos = intent.getIntExtra(DataSet.POSITION, -1)
        if (type == DataSet.Type.DIRECT_STATUS) {
            list = Home.whatsApplist
        } else if (type == DataSet.Type.SAVED_STATUS) {
            list = SavedView.savedWaList
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
        vp.adapter = MediaAdapter(this, list)
        vp.setCurrentItem(pos, false)
        binding.save.setOnClickListener {
            save(uriPath)
        }
        binding.share.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            val imagetUri = Uri.parse(uriPath)
            if (uriPath.endsWith(".mp4"))
                sharingIntent.type = "video/mp4"
            else sharingIntent.type = "image/jpeg"
            if (Build.VERSION.SDK_INT >= 24 && type == DataSet.Type.SAVED_STATUS) {
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
}