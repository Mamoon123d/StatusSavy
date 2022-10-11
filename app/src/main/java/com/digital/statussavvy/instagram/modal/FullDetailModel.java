package com.digital.statussavvy.instagram.modal;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/*
import papayacoders.instastory.models.ReelFeedModel;
import papayacoders.instastory.models.UserDetailModel;
*/

public class FullDetailModel implements Serializable {
    @SerializedName("reel_feed")
    private ReelFeedModel reelFeed;
    @SerializedName("user_detail")
    private UserDetailModel userDetail;

    public UserDetailModel getUserDetail() {
        return this.userDetail;
    }

    public void setUserDetail(UserDetailModel userDetailModel) {
        this.userDetail = userDetailModel;
    }

    public ReelFeedModel getReelFeed() {
        return this.reelFeed;
    }

    public void setReelFeed(ReelFeedModel reelFeedModel) {
        this.reelFeed = reelFeedModel;
    }
}