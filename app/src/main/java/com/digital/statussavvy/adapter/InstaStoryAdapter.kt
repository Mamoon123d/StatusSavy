package com.digital.statussavvy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.digital.statussavvy.databinding.InstaItemBinding
import com.digital.statussavvy.insta.InstaStatusDetails
import com.moon.baselibrary.base.BaseRvAdapter
import com.moon.baselibrary.base.BaseRvViewHolder

class InstaStoryAdapter(val context: Context, list: List<InstaStatusDetails>) :
    BaseRvAdapter<InstaStatusDetails, InstaStoryAdapter.MyHolder>(
        context, list
    ) {
    class MyHolder(val binding: InstaItemBinding) : BaseRvViewHolder(binding) {
        fun bind(context: Context, t: InstaStatusDetails) {
            val data = t.tray.user
            binding.userName.text = data.username
            Glide.with(context).load(data.profilePicUrl).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.instaImg)
        }

    }

    override fun onBindData(holder: MyHolder, t: InstaStatusDetails) {
        holder.bind(context, t)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val v = InstaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyHolder(v)
    }

}
