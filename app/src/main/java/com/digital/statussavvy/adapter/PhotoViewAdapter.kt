package com.digital.statussavvy.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.moon.baselibrary.base.BaseRvAdapter
import com.moon.baselibrary.base.BaseRvViewHolder

class PhotoViewAdapter(
    context: Context,
    list: List<String>
) : BaseRvAdapter<String, PhotoViewAdapter.MyHolder>(context, list) {
    class MyHolder(binding: ViewBinding) : BaseRvViewHolder(binding) {

    }

    override fun onBindData(holder: MyHolder, t: String) {
        TODO("Not yet implemented")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val
    }
}