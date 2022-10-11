package com.digital.statussavvy.insta;

public interface OnLogInListner {
    public static final String Facebook = "Facebook";
    public static final String Instagram = "Instagram";

    void onLogIn(String str);

    void onLogInFailed();
}
