package com.android.statussavvy.activity

import android.app.ProgressDialog
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.android.statussavvy.R
import com.android.statussavvy.adapter.PhotoViewAdapter
import com.android.statussavvy.base.BaseActivity
import com.android.statussavvy.data.ScreensData
import com.android.statussavvy.databinding.WallpaperViewBinding
import com.android.statussavvy.utils.Constent
import com.android.statussavvy.utils.DataSet
import com.android.statussavvy.utils.FileManager
import com.downloader.OnDownloadListener
import com.downloader.OnStartOrResumeListener
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.omega_r.libs.omegaintentbuilder.OmegaIntentBuilder
import com.omega_r.libs.omegaintentbuilder.downloader.DownloadCallback
import com.omega_r.libs.omegaintentbuilder.handlers.ContextIntentHandler
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors


class WallpaperView : BaseActivity<WallpaperViewBinding>() {

    private var position_: Int = -1
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
        position_ = pos
        val imagePath = Home.wallpaperlist[position_].originalUrl
        val leftArrow = binding.leftArrow
        val rightArrow = binding.rightArrow
        vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                position_ = position
                rightArrow.visibility = if (list!!.size == position + 1) View.GONE else View.VISIBLE
                leftArrow.visibility = if (position == 0) View.GONE else View.VISIBLE
            }
        })

        binding.download.setOnClickListener {
            val imagePath = Home.wallpaperlist[position_].originalUrl
            if (FileManager.getAvailableExternalMemory() > 1024 * 50) {
                startDownloading(imagePath)
            } else
                showMsg("No more space")
            //val downloadFile = DownloadTask("","","")
            //downloadFile.execute("the url to the file you want to download")
            /* myExecutor.execute {
                 mImage = mLoad(imagePath)
                 myHandler.post {
                     if (mImage != null) {
                         mSaveMediaToStorage(mImage)
                     }
                 }
             }*/

        }

        binding.share.setOnClickListener {
            shareItem()
        }

    }

    private fun shareItem() {
        val pd = ProgressDialog(this)
        pd.setTitle("please wait...")
        pd.show()
        val url = Home.wallpaperlist[position_].originalUrl
        OmegaIntentBuilder(this)
            .share()
            .filesUrls(url)
            .download(object : DownloadCallback {
                override fun onDownloaded(
                    success: Boolean,
                    contextIntentHandler: ContextIntentHandler
                ) {
                    pd.dismiss()
                    contextIntentHandler.startActivity()
                }
            })

    }


    fun startDownloading(imagePath: String) {
        val con = binding.downloadCon
        con.visibility = View.VISIBLE
        val filename = "${System.currentTimeMillis()}.jpg"

        val imagesDir =
            Constent.wallpaperSavePath

        val config = PRDownloaderConfig.newBuilder()
            .setDatabaseEnabled(true)
            .build()
        PRDownloader.initialize(
            this@WallpaperView,
            PRDownloaderConfig.newBuilder().setDatabaseEnabled(true)
                // .setReadTimeout(AbstractSpiCall.DEFAULT_TIMEOUT)
                //  .setConnectTimeout(AbstractSpiCall.DEFAULT_TIMEOUT)
                .build()
        )

        PRDownloader.download(
            imagePath,
            imagesDir,
            filename
        ).build()
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
                    binding.download.isClickable = false
                    binding.percentTv.text = sb2
                    binding.processPb.progress = i
                    binding.processPb.isIndeterminate = false
                }

            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {

                    showMsg("Download Completed")
                    con.visibility = View.GONE
                    binding.download.isClickable = true
                }

                override fun onError(error: com.downloader.Error?) {

                }

                fun onError(error: Error?) {

                }
            })
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
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + Constent.wallpaperSavePath)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show()
        }

    }

    /*open class DownloadService(binding: WallpaperViewBinding) : Service() {
        override fun onCreate() {
            super.onCreate()
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            return START_STICKY
        }

        override fun onDestroy() {
            super.onDestroy()
        }

        override fun onBind(intent: Intent?): IBinder? {
        }

    }*/
}