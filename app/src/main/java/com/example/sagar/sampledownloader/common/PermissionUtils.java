package com.example.sagar.sampledownloader.common;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by sagar on 6/10/17.
 */

public class PermissionUtils {

    private static final String TAG = PermissionUtils.class.getSimpleName();

    public static final int PERMISSION_REQUEST_CODE = 200;

    public static boolean checkPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED ;
    }

    public static void requestPermission(Activity activity) {
        if(checkPermission(activity.getApplicationContext())) return;
        ActivityCompat.requestPermissions(activity, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }
}
