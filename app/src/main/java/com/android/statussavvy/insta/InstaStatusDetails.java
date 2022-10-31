package com.android.statussavvy.insta;

public class InstaStatusDetails {
    private StatusHolder statusHolder;
    private StoriesTrayResponse.Tray tray;

    public InstaStatusDetails(StoriesTrayResponse.Tray tray2) {
        this.tray = tray2;
    }

    /* access modifiers changed from: package-private */
    public StoriesTrayResponse.Tray getTray() {
        return this.tray;
    }

    public void setTray(StoriesTrayResponse.Tray tray2) {
        this.tray = tray2;
    }

    /* access modifiers changed from: package-private */
    public StatusHolder getStatusHolder() {
        return this.statusHolder;
    }

    /* access modifiers changed from: package-private */
    public void setStatusHolder(StatusHolder statusHolder2) {
        this.statusHolder = statusHolder2;
    }
}
