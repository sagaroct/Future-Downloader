package com.example.sagar.sampledownloader.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.sagar.sampledownloader.common.CommonUtils;
import com.example.sagar.sampledownloader.common.Constants;
import com.example.sagar.sampledownloader.model.InternetType;
import com.example.sagar.sampledownloader.helper.DbHelper;
import com.example.sagar.sampledownloader.service.MultiDownloadService;

/**
 * Created by sagar on 22/7/16.
 */
public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmManagerBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "AlarmManagerBroadcastReceiver: Received");
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.WIFI_PREFERENCE, false)) {
            if((CommonUtils.getInternetConnectionType(context) == InternetType.WIFI)) {
                addToDbAndStartDownload(context, intent);
            }
        } else{
            addToDbAndStartDownload(context, intent);
        }
    }

    private void addToDbAndStartDownload(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();
        //You can do the processing here.
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Intent downloadIntent = new Intent(context, MultiDownloadService.class);
            downloadIntent.setAction(Constants.DOWNLOAD_PROGRESS_UPDATE);
            downloadIntent.putExtra(Constants.DOWNLOAD_URL, extras.getString(Constants.DOWNLOAD_URL));
            downloadIntent.putExtra(Constants.FILE_ID, extras.getLong(Constants.FILE_ID));
            DbHelper dbHelper = DbHelper.getInstance(context);
            dbHelper.addCurrentDownload(dbHelper.getFutureDownload(extras.getLong(Constants.FILE_ID)));
            dbHelper.deleteFutureDownload(extras.getLong(Constants.FILE_ID));
            context.startService(downloadIntent);
        }
        //Release the lock
        wl.release();
    }
}

