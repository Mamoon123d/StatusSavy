package com.digital.statussavvy.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.util.ArrayList;


public class MyPreference {
    public static SharedPreferences getPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferences.Editor editPreference(Context context) {
        return getPreference(context).edit();
    }

    public static void saveUri(Context c, Uri uri) {
        SharedPreferences.Editor e = editPreference(c);
        e.putString("urlPath", uri.toString());
        Log.d("TAG", "saveUri: " + uri.getPath());
        e.apply();
        e.commit();
    }
 public static String uriString(Context c){
        return getPreference(c).getString("urlPath","");
 }

    public static void setGrant(Context c) {
        SharedPreferences.Editor e = editPreference(c);
        e.putBoolean("isGrant", true);
        e.apply();
        e.commit();
    }

    public static Boolean isGranted(Context c) {
        return getPreference(c).getBoolean("isGrant", false);
    }

    public static Uri getUri(Context c) {
        String uriStr = getPreference(c).getString("urlPath", "");
        if (uriStr.isEmpty()) {
            return null;
        } else {
            if (isGranted(c)) {
                return Uri.parse(uriStr);
            }
            return null;
        }
    }

    public ArrayList<File> getUriList(Context c) {
        Uri uri = getUri(c);
        ArrayList<File> list = new ArrayList<>();
        if (uri != null) {
            if (Build.VERSION.SDK_INT >= 29) {
                // uri is the path which we've saved in our shared pref
                DocumentFile fromTreeUri = DocumentFile.fromTreeUri(c, uri);
                DocumentFile[] documentFiles = fromTreeUri.listFiles();


               /* for (int i = 0; i < documentFiles.length; i++) {
                    documentFiles[i].getUri().toString() //uri of the document
                }*/
                for (DocumentFile file : documentFiles) {
                    //list.add("")
                }
            }
        }
        return list;
    }





}
