package com.digital.statussavvy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.digital.statussavvy.databinding.TrendingItemBinding
import com.moon.baselibrary.base.BaseRvAdapter
import com.moon.baselibrary.base.BaseRvViewHolder

class WallPaperAdapter(
    val context: Context,
    list: List<String>
) : BaseRvAdapter<String, WallPaperAdapter.MyHolder>(context, list) {
    class MyHolder(val binding: TrendingItemBinding) : BaseRvViewHolder(binding) {
        fun bind(context: Context, t: String) {
            Glide.with(context).load(t).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.tendImg)
        }

    }

    override fun onBindData(holder: MyHolder, t: String) {
        holder.bind(context, t)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val v = TrendingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyHolder(v)
    }

}
