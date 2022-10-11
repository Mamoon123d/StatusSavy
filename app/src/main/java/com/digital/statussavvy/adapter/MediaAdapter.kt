package com.digital.statussavvy.adapter

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.digital.statussavvy.databinding.MediaItemBinding
import com.moon.baselibrary.base.BaseRvAdapter
import com.moon.baselibrary.base.BaseRvViewHolder

class MediaAdapter(
    val context: Context,
    list: List<String>
) : BaseRvAdapter<String, MediaAdapter.MyHolder>(context, list) {
    class MyHolder(val binding: MediaItemBinding) : BaseRvViewHolder(binding) {
        private lateinit var videoView: VideoView

        fun bind(context: Context, t: String) {
            if (!t.isNullOrBlank()) {
                //val uri= Uri.parse(t)

                if (t.endsWith(".mp4")) {
                    val uri = Uri.parse(t)
                    binding.photo.visibility = View.GONE
                    binding.video.visibility = View.VISIBLE

                    videoView = binding.video
                    videoView!!.setVideoURI(uri)
                    videoView!!.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
                        override fun onPrepared(mp: MediaPlayer?) {
                            mp!!.isLooping = true
                            videoView!!.start()
                        }

                    })
                } else {
                    binding.photo.visibility = View.VISIBLE
                    binding.video.visibility = View.GONE

                    Glide.with(context).asBitmap().load(t).centerCrop().fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.photo)

                    /* binding.photo.setOnPhotoTapListener { view, x, y ->
                         onZoomListener!!.onZoomChange(
                             binding.photo.isZoomable, true
                         )
                     }*/

                    /* binding.photo.setOnScaleChangeListener { scaleFactor, focusX, focusY ->
                     }*/
                }


            }
        }

    }

    override fun onBindData(holder: MyHolder, t: String) {
        holder.bind(context, t)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val v = MediaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyHolder(v)
    }
}