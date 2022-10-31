package com.android.statussavvy.FB

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.android.statussavvy.databinding.FbItemBinding
import com.google.android.exoplayer2.util.MimeTypes
import com.moon.baselibrary.base.BaseRvAdapter
import com.moon.baselibrary.base.BaseRvViewHolder
import java.io.File

class DownloadAdapter(val context: Context, list: List<DownloadDataHolder>) :
    BaseRvAdapter<DownloadDataHolder, DownloadAdapter.MyHolder>(
        context, list
    ) {
    class MyHolder(val binding: FbItemBinding) : BaseRvViewHolder(binding) {
        fun bind(context: Context, t: DownloadDataHolder) {
            binding.fileName.text = t.fileName!!.replace("\\d", "").replace(".mp", ".mp4")
            binding.location.text = t.uri
            try {
                Glide.with(context).load(t.uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.image)
            } catch (e: Exception) {

            }
            binding.share.setOnClickListener(View.OnClickListener {
                shareStatus(context, t.uri!!)
            })
            binding.play.setOnClickListener(View.OnClickListener {
                // val intent = Intent(context, ViewActivity::class.java)
                //intent.putExtra("uri", (downloadStatusList.get(i) as DownloadDataHolder).uri)
                //context.startActivity(intent)
            })
        }

        fun shareStatus(context: Context, str: String) {
            val intent = Intent("android.intent.action.SEND")
            val uriForFile =
                FileProvider.getUriForFile(context, context.packageName + ".provider", File(str))
            if (str.contains(".mp4")) {
                intent.type = MimeTypes.VIDEO_MP4
            } else {
                intent.type = "image/jpg"
            }
            //        intent.addFlags(1);
            intent.putExtra("android.intent.extra.STREAM", uriForFile)
            context.startActivity(Intent.createChooser(intent, "Share Story using"))
        }

    }

    override fun onBindData(holder: MyHolder, t: DownloadDataHolder) {
        holder.bind(context, t)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val v = FbItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyHolder(v)
    }

}
