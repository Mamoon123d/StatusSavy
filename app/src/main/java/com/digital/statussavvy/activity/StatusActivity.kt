package com.digital.statussavvy.activity

import android.content.ContentValues
import android.content.Context
import android.content.Intent
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.digital.statussavvy.R
import com.digital.statussavvy.databinding.StatusActivityBinding
import com.digital.statussavvy.insta.StatusHolder
import com.digital.statussavvy.insta.StatusList
import com.digital.statussavvy.utils.Constent
import com.digital.statussavvy.utils.DataSet
import com.digital.statussavvy.utils.StoryStatusView
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.util.Util
import com.moon.baselibrary.base.BaseActivity
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors


class StatusActivity : BaseActivity<StatusActivityBinding>(),
    StoryStatusView.UserInteractionListener {
    var storyStatusView: StoryStatusView? = null
    var statusList: ArrayList<StatusList> = ArrayList<StatusList>()
    var storyDuration = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS


    override fun setLayoutId(): Int {
        return R.layout.status_activity
    }

    override fun initM() {
        setStatus()
    }

    private fun setStatus() {
        this.statusList.toMutableList()
        statusList.clear()
        storyStatusView = binding.storiesStatus


        val list =
            (intent.extras!!.getParcelable(DataSet.Instagram.STATUS) as StatusHolder?)!!.statusList

        statusList.addAll(list)

        /*storyStatusView!!.setStoriesCount(statusList.size)
        storyStatusView!!.setStoryDuration(storyDuration, statusList)
        storyStatusView!!.setUserInteractionListener(this@StatusActivity)*/

        instaStatus()


    }

    private fun instaStatus() {

        Glide.with(this).load(statusList[0].profilePic).diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.profileImage)
        binding.userName.text = statusList[0].userName

        Glide.with(this).load(statusList[0].url).diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.statusImage)
        binding.progressbar.visibility = View.GONE
        binding.downloadingFramelayout.visibility = View.GONE


        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())
        var mImage: Bitmap?
        val imagePath = statusList[0].url
        binding.downloadButton.setOnClickListener {
            myExecutor.execute {
                mImage = mLoad(imagePath)
                myHandler.post {
                    if (mImage != null) {
                        mSaveMediaToStorage(mImage)
                    }
                }
            }
        }
        binding.shareButton.setOnClickListener {
            myExecutor.execute {
                mImage = mLoad(imagePath)
                if (mImage != null) {
                    share(mImage)

                }

            }
        }
    }

    private fun share(imageBitmap: Bitmap?) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "image/jpeg"
        // val bitmapPath: String = Images.Media.insertImage(contentResolver, bitmap, "title", null)
        val uri = getImageUri(this, imageBitmap!!)
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(sharingIntent, "Share image using"))

    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
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
                        Environment.DIRECTORY_PICTURES + Constent.InstaSavePath
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


    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            //    initializePlayer()
        }
        //playStorey()
    }

    /* private fun initializePlayer() {
         val newSimpleInstance: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this)
         this.player = newSimpleInstance
         this.playerView.setPlayer(newSimpleInstance)
         this.player.setPlayWhenReady(this.playWhenReady.toBoolean())
         this.player.seekTo(this.currentWindow, this.playbackPosition)
         this.player.addVideoListener(object : VideoListener() {
             fun onVideoSizeChanged(i: Int, i2: Int, i3: Int, f: Float) {}
             fun onRenderedFirstFrame() {
                 this@StatusActivity.handler = Handler()
                 this@StatusActivity.runnable = object : Runnable {
                     @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                     override fun run() {
                         if (this@StatusActivity.player != null) {
                             val currentPosition: Double =
                                 (this@StatusActivity.player.getCurrentPosition() * 100 / this@StatusActivity.player.getDuration()).toDouble()
                             storyStatusView!!.progressBar.interpolator =
                                 LinearInterpolator()
                             storyStatusView!!.progressBar.progress =
                                 currentPosition.toInt()
                             if (currentPosition < 100.0) {
                                 this@StatusActivity.handler.postDelayed(this, 0)
                                 return
                             }
                             this@StatusActivity.handler.removeCallbacks(this)
                             this@StatusActivity.onStatusNext()
                         }
                     }
                 }
                 this@StatusActivity.handler.postDelayed(this@StatusActivity.runnable, 0)
             }
         })
         this.player.addListener(object : EventListener() {
             fun onLoadingChanged(z: Boolean) {}
             fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}
             fun onPlayerError(exoPlaybackException: ExoPlaybackException?) {}
             fun onPositionDiscontinuity(i: Int) {}
             fun onRepeatModeChanged(i: Int) {}
             fun onSeekProcessed() {}
             fun onShuffleModeEnabledChanged(z: Boolean) {}
             fun onTimelineChanged(timeline: Timeline?, obj: Any?, i: Int) {}
             fun onTracksChanged(
                 trackGroupArray: TrackGroupArray?,
                 trackSelectionArray: TrackSelectionArray?
             ) {
             }

             fun onPlayerStateChanged(z: Boolean, i: Int) {
                 if (i == 3) {
                     this@StatusActivity.progressbar.setVisibility(View.INVISIBLE)
                 } else if (i == 2) {
                     this@StatusActivity.progressbar.setVisibility(View.VISIBLE)
                 }
             }
         })
     }*/

    override fun onImageStatusComplete() {

    }
}