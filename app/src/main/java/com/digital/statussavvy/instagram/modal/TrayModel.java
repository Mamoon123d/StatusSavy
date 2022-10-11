package com.digital.statussavvy.instagram.modal;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
/*
import papayacoders.instastory.models.ItemModel;
import papayacoders.instastory.models.UserModel;*/

public class TrayModel implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("items")
    private List<ItemModel> items;
    @SerializedName("media_count")
    private int mediacount;
    @SerializedName("user")
    private UserModel user;

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public UserModel getUser() {
        return this.user;
    }

    public void setUser(UserModel userModel) {
        this.user = userModel;
    }

    public int getMediacount() {
        return this.mediacount;
    }

    public void setMediacount(int i) {
        this.mediacount = i;
    }

    public List<ItemModel> getItems() {
        return this.items;
    }

    public void setItems(List<ItemModel> list) {
        this.items = list;
    }
}