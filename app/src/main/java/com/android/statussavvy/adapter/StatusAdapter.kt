package com.android.statussavvy.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.android.statussavvy.databinding.StatusItemBinding
import com.android.statussavvy.base.BaseRvAdapter
import com.android.statussavvy.base.BaseRvViewHolder

class StatusAdapter(
    val context: Context,
    list: List<String>
) : BaseRvAdapter<String, StatusAdapter.MyHolder>(context, list) {
    class MyHolder(val binding: StatusItemBinding) : BaseRvViewHolder(binding) {

        fun bind(context: Context, t: String) {
            Glide.with(context).load(Uri.parse(t)).into(binding.sImg)
            // binding.sImg.setImageURI(t.toUri())
        }

    }

    override fun onBindData(holder: MyHolder, t: String) {

        holder.bind(context, t)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val v = StatusItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyHolder(v)
    }

}
