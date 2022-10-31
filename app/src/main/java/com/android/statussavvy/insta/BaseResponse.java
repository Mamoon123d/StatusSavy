package com.android.statussavvy.insta;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {
    @SerializedName("status")
    protected String status;

    public String getStatus() {
        return this.status;
    }
}
