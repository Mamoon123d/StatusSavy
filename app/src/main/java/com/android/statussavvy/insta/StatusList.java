package com.android.statussavvy.insta;

import android.animation.ObjectAnimator;
import android.os.Parcel;
import android.os.Parcelable;

public class StatusList implements Parcelable {
    public static final Creator<StatusList> CREATOR = new Creator<StatusList>() {
        public StatusList createFromParcel(Parcel parcel) {
            return new StatusList(parcel);
        }

        public StatusList[] newArray(int i) {
            return new StatusList[i];
        }
    };
    private String URL;
    private String fullName;
    public Boolean isVideoStatus;
    private ObjectAnimator objectAnimator;
    private String profilePic;
    private String thumbnail;
    private String userName;

    public int describeContents() {
        return 0;
    }

    public StatusList(Boolean bool, String str, String str2, String str3, String str4) {
        this.isVideoStatus = bool;
        this.URL = str;
        this.profilePic = str2;
        this.userName = str3;
        this.fullName = str4;
    }

    public StatusList(Boolean bool, String str, String str2, String str3, String str4, String str5) {
        this.isVideoStatus = bool;
        this.URL = str;
        this.profilePic = str3;
        this.thumbnail = str2;
        this.userName = str4;
        this.fullName = str5;
    }

    private StatusList(Parcel parcel) {
        this.isVideoStatus = (Boolean) parcel.readValue(Boolean.class.getClassLoader());
        this.URL = parcel.readString();
        this.profilePic = parcel.readString();
        this.thumbnail = parcel.readString();
        this.userName = parcel.readString();
        this.fullName = parcel.readString();
    }

    public String getProfilePic() {
        return this.profilePic;
    }

    public ObjectAnimator getObjectAnimator() {
        return this.objectAnimator;
    }

    /* access modifiers changed from: package-private */
    public void setObjectAnimator(ObjectAnimator objectAnimator2) {
        this.objectAnimator = objectAnimator2;
    }

    public String getURL() {
        return this.URL;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeValue(this.isVideoStatus);
        parcel.writeString(this.URL);
        parcel.writeString(this.profilePic);
        parcel.writeString(this.thumbnail);
        parcel.writeString(this.userName);
        parcel.writeString(this.fullName);
    }
}
