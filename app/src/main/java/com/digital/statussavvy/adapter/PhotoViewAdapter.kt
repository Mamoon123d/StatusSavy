package com.digital.statussavvy.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.digital.statussavvy.data.ScreensData
import com.digital.statussavvy.databinding.PhotoItemBinding
import com.moon.baselibrary.base.BaseRvAdapter
import com.moon.baselibrary.base.BaseRvViewHolder


class PhotoViewAdapter(
    val context: Context,
    list: List<ScreensData.Wallpaper>
) : BaseRvAdapter<ScreensData.Wallpaper, PhotoViewAdapter.MyHolder>(context, list) {
    class MyHolder(val binding: PhotoItemBinding) : BaseRvViewHolder(binding) {

        fun bind(context: Context, t: ScreensData.Wallpaper) {
            // Glide.with(context).load(t.smallUrl).into(binding.photoView)
            val thumbnailRequest: RequestBuilder<Drawable> = Glide
                .with(context)
                .load(t.smallUrl)
            val progress = binding.imageProgress

            Glide
                .with(context)
                .load(t.originalUrl)
                .thumbnail(thumbnailRequest)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progress.visibility = View.GONE
                        return false
                    }


                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progress.visibility = View.GONE
                        return false
                    }

                })
                .into(binding.photoView)


        }

    }

    override fun onBindData(holder: MyHolder, t: ScreensData.Wallpaper) {
        holder.bind(context, t)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val v = PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyHolder(v)
    }
}