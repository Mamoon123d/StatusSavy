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
import com.moon.baselibrary.base.BaseActivity
import com.moon.baselibrary.base.BaseRvAdapter
import java.io.File

class SavedView : BaseActivity<SavedViewBinding>() {

    companion object{
       public var savedWaList = mutableListOf<String>()
    }

    override fun setLayoutId(): Int {
        return R.layout.saved_view
    }

    override fun initM() {
        setWhatsAppStatus()
    }

    private fun setWhatsAppStatus() {
        val rv = binding.WaStatusRv
        val imageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + Constent.SavePath)

        val folder = File(imageDir.absolutePath)
        savedWaList.toMutableList()
        if (folder.exists()) {

            for (file in folder.listFiles()!!) {
                savedWaList.add(file.toUri().toString())
            }
        }

        val adapter = WaStatusAdapter(this, savedWaList)
        rv.adapter = adapter
        rv.layoutManager = GridLayoutManager(this, 3)
        adapter.setOnItemClickListener(object : BaseRvAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                val b = Bundle()
                b.putByte(DataSet.Type.TYPE, DataSet.Type.SAVED_STATUS)
                b.putInt(DataSet.POSITION,position)
                goActivity(MediaView(), b)
            }

        })
    }


}