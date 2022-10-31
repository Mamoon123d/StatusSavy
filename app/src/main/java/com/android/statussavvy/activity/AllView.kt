package com.android.statussavvy.activity

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedDispatcher
import androidx.recyclerview.widget.GridLayoutManager
import com.android.statussavvy.R
import com.android.statussavvy.adapter.WaStatusAdapter
import com.android.statussavvy.databinding.AllViewBinding
import com.android.statussavvy.utils.DataSet
import com.android.statussavvy.base.BaseActivity
import com.android.statussavvy.base.BaseRvAdapter

class AllView : BaseActivity<AllViewBinding>() {
    override fun setLayoutId(): Int {
        return R.layout.all_view
    }

    override fun initM() {
        setContent()

    }

    private fun setTb(type: Byte) {
        binding.closeBtn.setOnClickListener {
            OnBackPressedDispatcher().onBackPressed()
        }
        if (type == DataSet.Type.DIRECT_STATUS) {
            //for whatsapp
            binding.title.text = getString(R.string.whatsapp_status)
        }else if (type == DataSet.Type.WALLPAPERS) {
            //for whatsapp
            "Wallpapers".also { binding.title.text = it }
        }
        binding.closeBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
