package com.example.neo.downloader;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Neo on 20/11/2016.
 */

public class CheckDownloadComplete extends BroadcastReceiver {
    public static boolean isDownloadComplete = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        isDownloadComplete = true;
        if (CheckDownloadComplete.isDownloadComplete) {
            //code after completion of download
            Toast.makeText(context, context.getResources().getString(R.string.down_success), Toast.LENGTH_SHORT).show();
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notif)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setContentText(context.getResources().getString(R.string.thanks));
        Intent resultIntent = new Intent(context, Download.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}