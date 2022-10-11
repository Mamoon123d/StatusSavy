package com.digital.statussavvy.insta;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class StatusHolder implements Parcelable {
    public static final Creator<StatusHolder> CREATOR = new Creator<StatusHolder>() {
        public StatusHolder createFromParcel(Parcel parcel) {
            return new StatusHolder(parcel);
        }

        public StatusHolder[] newArray(int i) {
            return new StatusHolder[i];
        }
    };
    private List<StatusList> StatusList;

    public int describeContents() {
        return 0;
    }

    public StatusHolder() {
        this.StatusList = new ArrayList();
    }

    private StatusHolder(Parcel parcel) {
        this.StatusList = new ArrayList();
        ArrayList arrayList = new ArrayList();
        this.StatusList = arrayList;
        parcel.readList(arrayList, StatusList.class.getClassLoader());
    }

    public List<StatusList> getStatusList() {
        return this.StatusList;
    }

    public void setStatusList(ArrayList<StatusList> list) {
        this.StatusList = list;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(this.StatusList);
    }
}
