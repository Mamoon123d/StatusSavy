package com.digital.statussavvy.activity

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import com.digital.statussavvy.R
import com.digital.statussavvy.adapter.PhotoViewAdapter
import com.digital.statussavvy.data.ScreensData
import com.digital.statussavvy.databinding.WallpaperViewBinding
import com.digital.statussavvy.utils.Constent
import com.digital.statussavvy.utils.DataSet
import com.moon.baselibrary.base.BaseActivity
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors

class WallpaperView : BaseActivity<WallpaperViewBinding>() {

    private var list: List<ScreensData.Wallpaper>? = null

    override fun setLayoutId(): Int {
        return R.layout.wallpaper_view
    }

    override fun initM() {
        setPhoto()
    }

    private fun setPhoto() {
        val pos = intent.getIntExtra(DataSet.POSITION, -1)
        val vp = binding.vp
        list = Home.wallpaperlist
        val adapter = PhotoViewAdapter(this, list!!)
        vp.adapter = adapter
        vp.setCurrentItem(pos, false)
        var mImage: Bitmap?
        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())
        val imagePath = Home.wallpaperlist[pos].originalUrl

        binding.download.setOnClickListener {
            myExecutor.execute {
                mImage = mLoad(imagePath)
                myHandler.post {
                    if (mImage != null) {
                        mSaveMediaToStorage(mImage)
                    }
                }
            }
        }
    }

    private fun mLoad(string: String): Bitmap? {
        val url: URL = mStringToURL(string)!!
        val connection: HttpURLConnection?
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
            return BitmapFactory.decodeStream(bufferedInputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
        }
        return null
    }

    private fun mStringToURL(string: String): URL? {
        try {
            return URL(string)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }

    private fun mSaveMediaToStorage(bitmap: Bitmap?) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + Constent.wallpaperSavePath
                    )
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show()
        }
    }
}