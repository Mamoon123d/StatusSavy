package com.digital.statussavvy.activity

import android.os.Build
import android.os.Handler
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.digital.statussavvy.R
import com.digital.statussavvy.databinding.StatusActivityBinding
import com.digital.statussavvy.insta.StatusHolder
import com.digital.statussavvy.insta.StatusList
import com.digital.statussavvy.utils.DataSet
import com.digital.statussavvy.utils.StoryStatusView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.util.Util
import com.moon.baselibrary.base.BaseActivity

class StatusActivity : BaseActivity<StatusActivityBinding>(),
    StoryStatusView.UserInteractionListener {
    var storyStatusView: StoryStatusView? = null
    var statusList: ArrayList<StatusList> = ArrayList<StatusList>()
    var storyDuration = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS


    override fun setLayoutId(): Int {
        return R.layout.status_activity
    }

    override fun initM() {
        setStatus()
    }

    private fun setStatus() {
        this.statusList.toMutableList()
        statusList.clear()
        storyStatusView=binding.storiesStatus


        val list=(intent.extras!!.getParcelable(DataSet.Instagram.STATUS) as StatusHolder?)!!.statusList

        statusList.addAll(list)

             storyStatusView!!.setStoriesCount(statusList.size)
             storyStatusView!!.setStoryDuration(storyDuration, statusList)
            storyStatusView!!.setUserInteractionListener(this@StatusActivity)

        Glide.with(this).load(statusList[0].profilePic).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.profileImage)
        binding.userName.text = statusList[0].userName

    }
    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
        //    initializePlayer()
        }
        //playStorey()
    }

   /* private fun initializePlayer() {
        val newSimpleInstance: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this)
        this.player = newSimpleInstance
        this.playerView.setPlayer(newSimpleInstance)
        this.player.setPlayWhenReady(this.playWhenReady.toBoolean())
        this.player.seekTo(this.currentWindow, this.playbackPosition)
        this.player.addVideoListener(object : VideoListener() {
            fun onVideoSizeChanged(i: Int, i2: Int, i3: Int, f: Float) {}
            fun onRenderedFirstFrame() {
                this@StatusActivity.handler = Handler()
                this@StatusActivity.runnable = object : Runnable {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    override fun run() {
                        if (this@StatusActivity.player != null) {
                            val currentPosition: Double =
                                (this@StatusActivity.player.getCurrentPosition() * 100 / this@StatusActivity.player.getDuration()).toDouble()
                            storyStatusView!!.progressBar.interpolator =
                                LinearInterpolator()
                            storyStatusView!!.progressBar.progress =
                                currentPosition.toInt()
                            if (currentPosition < 100.0) {
                                this@StatusActivity.handler.postDelayed(this, 0)
                                return
                            }
                            this@StatusActivity.handler.removeCallbacks(this)
                            this@StatusActivity.onStatusNext()
                        }
                    }
                }
                this@StatusActivity.handler.postDelayed(this@StatusActivity.runnable, 0)
            }
        })
        this.player.addListener(object : EventListener() {
            fun onLoadingChanged(z: Boolean) {}
            fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}
            fun onPlayerError(exoPlaybackException: ExoPlaybackException?) {}
            fun onPositionDiscontinuity(i: Int) {}
            fun onRepeatModeChanged(i: Int) {}
            fun onSeekProcessed() {}
            fun onShuffleModeEnabledChanged(z: Boolean) {}
            fun onTimelineChanged(timeline: Timeline?, obj: Any?, i: Int) {}
            fun onTracksChanged(
                trackGroupArray: TrackGroupArray?,
                trackSelectionArray: TrackSelectionArray?
            ) {
            }

            fun onPlayerStateChanged(z: Boolean, i: Int) {
                if (i == 3) {
                    this@StatusActivity.progressbar.setVisibility(View.INVISIBLE)
                } else if (i == 2) {
                    this@StatusActivity.progressbar.setVisibility(View.VISIBLE)
                }
            }
        })
    }*/

    override fun onImageStatusComplete() {

    }
}