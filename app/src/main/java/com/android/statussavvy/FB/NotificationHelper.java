package com.android.statussavvy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.statussavvy.FB.DownloadActivity;


public class NotificationHelper {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String NOTIFICATION_CHANNEL_ID = "10001";
    private NotificationCompat.Builder mBuilder;
    private Context mContext;
    private NotificationManager mNotificationManager;

    public NotificationHelper(Context context) {
        this.mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createNotification(String str, String str2) {
        Intent intent = new Intent(this.mContext, DownloadActivity.class);
//        intent.addFlags(C0819C.ENCODING_PCM_MU_LAW);
        PendingIntent activity = PendingIntent.getActivity(this.mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.mContext);
        this.mBuilder = builder;
        builder.setSmallIcon(R.mipmap.ic_launcher);
        this.mBuilder.setContentTitle(str).setContentText(str2).setAutoCancel(false).setSound(Settings.System.DEFAULT_NOTIFICATION_URI).setContentIntent(activity);
        this.mNotificationManager = (NotificationManager) this.mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
           // notificationChannel.setLightColor(SupportMenu.CATEGORY_MASK);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            this.mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            this.mNotificationManager.createNotificationChannel(notificationChannel);
        }
        this.mNotificationManager.notify(0, this.mBuilder.build());
    }
}
