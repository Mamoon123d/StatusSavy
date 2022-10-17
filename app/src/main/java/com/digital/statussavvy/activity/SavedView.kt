package com.digital.statussavvy.activity

import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import com.digital.statussavvy.R
import com.digital.statussavvy.adapter.WaStatusAdapter
import com.digital.statussavvy.databinding.SavedViewBinding
import com.digital.statussavvy.utils.Constent
import com.digital.statussavvy.utils.DataSet
import com.digital.statussavvy.utils.MyPreference
import com.moon.baselibrary.base.BaseActivity
import com.moon.baselibrary.base.BaseRvAdapter
import java.io.File

class SavedView : BaseActivity<SavedViewBinding>() {

    companion object {
        public var savedWaList = mutableListOf<String>()
        public var savedWallpaperList = mutableListOf<String>()
        public var instaStatusList = mutableListOf<String>()
    }

    override fun onResume() {
        super.onResume()
        val isChange = MyPreference.isGranted(this)
        if (isChange) {
            MyPreference.setChange(this, false)
            setWhatsAppStatus()
            setWallpaper()
            setInstaStatus()
            listener()

        }
    }

    override fun setLayoutId(): Int {
        return R.layout.saved_view
    }

    override fun initM() {

        setWhatsAppStatus()
        setWallpaper()
        setInstaStatus()
        listener()
    }

    private fun listener() {
        binding.closeBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setInstaStatus() {
        val imageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + Constent.InstaSavePath)
        val folder = File(imageDir.absolutePath)
        instaStatusList.clear()
        savedWallpaperList.toMutableList()
        if (folder.exists()) {
            for (file in folder.listFiles()!!) {
                instaStatusList.add(file.toUri().toString())
            }
        }
        if (instaStatusList.size == 0) {
            binding.instagramTv.visibility = View.GONE
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
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + Constent.wallpaperSavePath)
        val folder = File(imageDir.absolutePath)
        savedWallpaperList.clear()
        savedWallpaperList.toMutableList()
        if (folder.exists()) {

            for (file in folder.listFiles()!!) {
                savedWallpaperList.add(file.toUri().toString())
            }
        }
        if (savedWallpaperList.size == 0) {
            binding.wallpaperTv.visibility = View.GONE
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