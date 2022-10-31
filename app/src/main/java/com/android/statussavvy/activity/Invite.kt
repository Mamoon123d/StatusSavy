package com.android.statussavvy.activity

import android.view.View
import android.widget.Toast
import com.android.statussavvy.R
import com.android.statussavvy.base.BaseActivity
import com.android.statussavvy.data.InviteData
import com.android.statussavvy.databinding.InviteBinding
import com.android.statussavvy.network.ApiClient
import com.android.statussavvy.network.ApiRequest
import com.android.statussavvy.network.NetworkHelper
import com.android.statussavvy.utils.SendTo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Invite : BaseActivity<InviteBinding>() {
    override fun setLayoutId(): Int {
        return R.layout.invite
    }

    override fun initM() {
        setTb()
        setContent()
    }

    private fun setContent() {
        val progress = binding.progress
        val parentCon = binding.parent
        val isNetwork = NetworkHelper.isNetworkAvailable(this)
        progress.visibility = View.VISIBLE
        if (isNetwork) {
            val call = ApiClient.getApi().getInviteData(ApiRequest.createRequest(this))
            call.enqueue(object : Callback<InviteData> {
                override fun onResponse(call: Call<InviteData>, response: Response<InviteData>) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            progress.visibility = View.GONE
                            parentCon.visibility = View.VISIBLE
                            setSocialListener(data.inviteTextUrl, data.inviteFbUrl)
                            binding.codeTv.text = data.referralCode + ""
                            binding.inviteText.text = data.inviteText
                        }
                    }
                }

                override fun onFailure(call: Call<InviteData>, t: Throwable) {
                }

            })
        } else Toast.makeText(this, "Network Not Available", Toast.LENGTH_SHORT).show()

    }

    private fun setSocialListener(msg: String, fbUrl: String) {
        val sendTo = SendTo(this)
        binding.whatsappIv.setOnClickListener {
            sendTo.Whatsapp(msg)
        }
        binding.facebookIv.setOnClickListener {
            sendTo.Facebook(msg, fbUrl)
        }
        binding.telegramIv.setOnClickListener {
            sendTo.Telegram(msg)
        }
        binding.moreIv.setOnClickListener {
            sendTo.More(msg)
        }

    }

    private fun setTb() {
        binding.closeBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}