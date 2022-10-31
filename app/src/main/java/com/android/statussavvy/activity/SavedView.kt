package com.android.statussavvy.activity

import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import com.android.statussavvy.R
import com.android.statussavvy.adapter.WaStatusAdapter
import com.android.statussavvy.base.BaseActivity
import com.android.statussavvy.base.BaseRvAdapter
import com.android.statussavvy.databinding.SavedViewBinding
import com.android.statussavvy.utils.Constent
import com.android.statussavvy.utils.DataSet
import com.android.statussavvy.utils.MyPreference
import java.io.File

class SavedView : BaseActivity<SavedViewBinding>() {
    companion object {
        var savedWaList = mutableListOf<String>()
        var savedWallpaperList = mutableListOf<String>()
        var instaStatusList = mutableListOf<String>()
        var downloadedList = mutableListOf<String>()
    }

    var isNoItem: Boolean = false
    override fun onResume() {
        super.onResume()
        val isChange = MyPreference.isGranted(this)
        if (isChange) {
            MyPreference.setChange(this, false)
            setWhatsAppStatus()
            setWallpaper()
            setInstaStatus()
            setDownloadContent()
            listener()
          // noItem(isNoItem)
        }
    }

    private fun setDownloadContent() {

        val imageDir = Constent.dwnSavePath

        val folder = File(imageDir)
        downloadedList.clear()
        downloadedList.toMutableList()
        if (folder.exists()) {
            for (file in folder.listFiles()!!) {
                downloadedList.add(file.toUri().toString())
            }
        }
        if (downloadedList.size == 0) {
            binding.downloadTv.visibility = View.GONE
            isNoItem=true

        }
        val rv = binding.downloadedRv
        val adapter = WaStatusAdapter(this, downloadedList)
        rv.adapter = adapter
        rv.layoutManager = GridLayoutManager(this, 3)

        adapter.setOnItemClickListener(object : BaseRvAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                val b = Bundle()
                b.putByte(DataSet.Type.TYPE, DataSet.Type.SAVED_DOWNLOAD)
                b.putInt(DataSet.POSITION, position)
                goActivity(MediaView(), b)

            }
        })
    }

    override fun setLayoutId(): Int {
        return R.layout.saved_view
    }

    override fun initM() {

        setWhatsAppStatus()
        setWallpaper()
        setInstaStatus()
        setDownloadContent()
        listener()
      //  noItem(isNoItem)

        //noItem(false)

    }

    private fun noItem(isNo: Boolean) {
        val noItem = binding.noItem
        if (isNo) {
            noItem.visibility = View.VISIBLE
        } else
            noItem.visibility = View.VISIBLE

    }

    private fun listener() {
        binding.closeBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setInstaStatus() {
        // val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + Constent.InstaSavePath)
        val imageDir = Constent.igSavePath

        val folder = File(imageDir)
        instaStatusList.clear()
        savedWallpaperList.toMutableList()
        if (folder.exists()) {
            for (file in folder.listFiles()!!) {
                instaStatusList.add(file.toUri().toString())
            }
        }
        if (instaStatusList.size == 0) {
            binding.instagramTv.visibility = View.GONE
            isNoItem=true

        }
        val rv = binding.instaStatusRv
        val adapter = WaStatusAdapter(this, instaStatusList)
        rv.adapter = adapter
        rv.layoutManager = GridLayoutManager(this, 3)

        adapter.setOnItemClickListener(object : BaseRvAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                val b = Bundle()
                b.putByte(DataSet.Type.TYPE, DataSet.Type.SAVED_INSTA)
                b.putInt(DataSet.POSITION, position)
                goActivity(MediaView(), b)

            }
        })
    }

    private fun setWallpaper() {
        //savedWallpaperList.clear()
        val imageDir =
            Constent.wallpaperSavePath
        val folder = File(imageDir)
        savedWallpaperList.clear()
        savedWallpaperList.toMutableList()
        if (folder.exists()) {

            for (file in folder.listFiles()!!) {
                if (file.toUri().toString().endsWith(".jpg")) {
                    savedWallpaperList.add(file.toUri().toString())
                }
            }
        }
        if (savedWallpaperList.size == 0) {
            binding.wallpaperTv.visibility = View.GONE
            isNoItem=true

        }

        val rv = binding.wallRv
        val adapter = WaStatusAdapter(this, savedWallpaperList)
        rv.adapter = adapter
        rv.layoutManager = GridLayoutManager(this, 3)
        adapter.setOnItemClickListener(object : BaseRvAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                val b = Bundle()
                b.putByte(DataSet.Type.TYPE, DataSet.Type.SAVED_WALLPAPERS)
                b.putInt(DataSet.POSITION, position)
                goActivity(MediaView(), b)
            }
            // https://www.facebook.com/reel/849250753105638?s=yWDuG2&fs=e

        })


    }


    private fun setWhatsAppStatus() {
        val imageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + Constent.SavePath)
        val folder = File(imageDir.absolutePath)
        savedWaList.clear()
        savedWaList.toMutableList()
        if (folder.exists()) {
            for (file in folder.listFiles()!!) {
                savedWaList.add(file.toUri().toString())
            }
        }
        if (savedWaList.size < 1) {
            binding.whatsappTv.visibility = View.GONE
            isNoItem=true
        }
        val rv = binding.WaStatusRv
        val adapter = WaStatusAdapter(this, savedWaList)
        rv.adapter = adapter
        rv.layoutManager = GridLayoutManager(this, 3)

        adapter.setOnItemClickListener(object : BaseRvAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                val b = Bundle()
                b.putByte(DataSet.Type.TYPE, DataSet.Type.SAVED_STATUS)
                b.putInt(DataSet.POSITION, position)
                goActivity(MediaView(), b)
            }

        })

    }


}