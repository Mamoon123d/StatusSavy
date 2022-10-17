package com.digital.statussavvy.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.digital.statussavvy.R
import com.digital.statussavvy.adapter.WaStatusAdapter
import com.digital.statussavvy.databinding.AllViewBinding
import com.digital.statussavvy.utils.DataSet
import com.moon.baselibrary.base.BaseActivity
import com.moon.baselibrary.base.BaseRvAdapter

class AllView : BaseActivity<AllViewBinding>() {
    override fun setLayoutId(): Int {
        return R.layout.all_view
    }

    override fun initM() {
        setContent()

    }

    private fun setTb(type: Byte) {
        binding.closeBtn.setOnClickListener {
            onBackPressed()
        }
        if (type == DataSet.Type.DIRECT_STATUS) {
            //for whatsapp
            binding.title.text = "WhatsApp Status"
        }else if (type == DataSet.Type.WALLPAPERS) {
            //for whatsapp
            binding.title.text = "Wallpapers"
        }
    }

    private fun setContent() {
        val type = intent.getByteExtra(DataSet.Type.TYPE, -1)
        if (type == DataSet.Type.DIRECT_STATUS) {
            //for whatsapp
            setWhatsAppStatus()
        } else if (type == DataSet.Type.WALLPAPERS) {
                //for wallpapers
                setWallpapers()
            }
        setTb(type)
    }

    private fun setWallpapers() {
        val rv = binding.showAllRv
        val list= mutableListOf<String>()
        for (t in Home.wallpaperlist){
            list.add(t.smallUrl)
        }
        val adapter = WaStatusAdapter(this, list)
        rv.adapter = adapter
        rv.layoutManager = GridLayoutManager(this, 3)
        adapter.setOnItemClickListener(object : BaseRvAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                val b = Bundle()
                b.putInt(DataSet.POSITION, position)
                b.putByte(DataSet.Type.TYPE, DataSet.Type.WALLPAPERS)
                goActivity(WallpaperView(), b)
            }

        })
    }

    private fun setWhatsAppStatus() {
        val rv = binding.showAllRv
        val adapter = WaStatusAdapter(this, Home.whatsApplist)
        rv.adapter = adapter
        rv.layoutManager = GridLayoutManager(this, 3)
        adapter.setOnItemClickListener(object : BaseRvAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                val b = Bundle()
                b.putInt(DataSet.POSITION, position)
                b.putByte(DataSet.Type.TYPE, DataSet.Type.DIRECT_STATUS)
                goActivity(MediaView(), b)
            }

        })
    }

}
