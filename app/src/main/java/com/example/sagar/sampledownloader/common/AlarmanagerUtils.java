package com.example.sagar.sampledownloader.common;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.sagar.sampledownloader.R;
import com.example.sagar.sampledownloader.receiver.AlarmManagerBroadcastReceiver;

/**
 * Created by sagar on 17/8/16.
 */
public class AlarmanagerUtils {

    public static final String TAG = AlarmanagerUtils.class.getSimpleName();

    public static void setDownloadTimer(Context context, String url, long fileId, long dateTimeInMillis) {
        Log.d(TAG, "url: " + url + "\ndateTimeInMillis: " + dateTimeInMillis + "\nSystem.currenttime: " + System.currentTimeMillis());
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(Constants.DOWNLOAD_URL, url);
        intent.putExtra(Constants.FILE_ID, fileId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)fileId, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, dateTimeInMillis, pendingIntent);
    }

    public static void setDownloadTimer(Context context, String url, long fileId, long dateTimeInMillis, String startDate, String startTime,
                                        String stopTime, boolean repeatDownload) {
        if(stopTime.equalsIgnoreCase(context.getString(R.string.stop_time))){
            if(repeatDownload) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
                intent.putExtra(Constants.DOWNLOAD_URL, url);
                intent.putExtra(Constants.FILE_ID, fileId);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, dateTimeInMillis, pendingIntent);
//                alarmManager.set(AlarmManager.RTC_WAKEUP)
            }
            else{

            }
        }
    }

    public static void cancelDownloadFromAlarmManager(Context context, long fileId, String url) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(Constants.DOWNLOAD_URL, url);
        intent.putExtra(Constants.FILE_ID, fileId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) fileId, intent, 0);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

}
