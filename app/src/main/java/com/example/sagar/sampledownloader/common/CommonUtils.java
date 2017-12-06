package com.example.sagar.sampledownloader.common;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.example.sagar.sampledownloader.BuildConfig;
import com.example.sagar.sampledownloader.model.InternetType;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sagar on 20/7/16.
 */
public class CommonUtils {

    private static final String TAG = CommonUtils.class.getSimpleName();

    private final static AtomicInteger c = new AtomicInteger(0);
    private static final long FIVE_HALF_HOURS_MILLIS = 19800000;

    /**
     * @param size
     * @return
     */
    public static String getStringByteSize(long size) {
        if (size > 1024 * 1024)  //mega
        {
            return String.format("%.1f MB", size / (float) (1024 * 1024));
        } else if (size > 1024)  //kilo
        {
            return String.format("%.1f KB", size / 1024.0f);
        } else {
            return String.format("%d B", size);
        }
    }

    public static int getUniqueIdForNotification() {
        LogUtils.logD(TAG, "UniqueIdForNotification: " + c.incrementAndGet());
        return c.incrementAndGet();
    }

    public static long getMillisecondsFromDateTime(String strDate, String time) {
        DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
        Date startDate = null;
        Date timeDate = null;
        try {
            startDate = df.parse(strDate);
            LogUtils.logD(TAG, "startDate: " + startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.TIME_FORMAT);
        try {
            timeDate = dateFormat.parse(time);
            LogUtils.logD(TAG, "timeDate: " + timeDate);
        } catch (ParseException e) {
        }
        if (startDate != null && timeDate != null) {
            LogUtils.logD(TAG, "startDate: " + startDate.getTime() + "\ntimeDateMillis: " + timeDate.getTime());
            LogUtils.logD(TAG, "Final time after addition: " + startDate.getTime() + timeDate.getTime() + FIVE_HALF_HOURS_MILLIS);
            return startDate.getTime() + timeDate.getTime() + FIVE_HALF_HOURS_MILLIS;
        } else {
            return 0;
        }
    }

    public static String pad(int input) {
        String str;
        if (input >= 10) {
            str = Integer.toString(input);
        } else {
            str = "0" + Integer.toString(input);
        }
        return str;
    }

    public static String getFilePath(Context context, String fileName) {
            return getStoragePath(context) + "/" + getFileName(context, fileName);
    }

    private static String getStoragePath(Context context){
        String filePath = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.PATH_PREFERENCE, "");
        if (!TextUtils.isEmpty(filePath) && isValidDirectory(filePath)) {
            return filePath;
        } else {
            return Environment.getExternalStorageDirectory().toString();
        }
    }

    public static String getFilePathWithoutFileNameChanged(Context context, String fileName) {
        String filePath = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.PATH_PREFERENCE, "");
        if (!TextUtils.isEmpty(filePath) && isValidDirectory(filePath)) {
            return filePath + "/" + fileName;
        } else {
            return Environment.getExternalStorageDirectory() + "/" + fileName;
        }
    }

    public static String getFileName(Context context, String fileNameWithExtension){
        String extension = fileNameWithExtension.substring(fileNameWithExtension.lastIndexOf("."));
        String fileName = fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf("."));
        String[] allFiles = new File(getStoragePath(context)).list();
        int sameFileCount = 0;
        if(allFiles!=null) {
            for (String file : allFiles) {
                if (file.matches(fileName + "\\d*" + extension) || file.equals(fileName + extension)) {
                    sameFileCount++;
                }
            }
        }
        if(sameFileCount > 0) {
            return fileName + sameFileCount + extension;
        }
        else{
            return fileNameWithExtension;
        }
    }

    public static String getFileDirectory(Context context) {
        String filePath = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.PATH_PREFERENCE, "");
        if (!TextUtils.isEmpty(filePath) && isValidDirectory(filePath)) {
            return filePath;
        } else {
            return "" + Environment.getExternalStorageDirectory();
        }
    }

    private static boolean isValidDirectory(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            return file.exists();      // First, make sure the path exists
        }
        return false;
    }

    public static boolean isDateAfterCurrentDay(Date date) {
        return new Date().after(date);
    }

    public static void openFile(Context context, Uri uri) throws Exception {
        // Create URI
       /* File file=url;
        Uri uri = Uri.fromFile(file);*/
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (uri.toString().contains(".doc") || uri.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (uri.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (uri.toString().contains(".ppt") || uri.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (uri.toString().contains(".xls") || uri.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (uri.toString().contains(".zip") || uri.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/zip");
        } else if (uri.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (uri.toString().contains(".wav") || uri.toString().contains(".mp3")) {
            // WAV audio file
//            intent.setDataAndType(uri, "audio/x-wav");
            intent.setDataAndType(uri, "audio/*");
        } else if (uri.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (uri.toString().contains(".jpg") || uri.toString().contains(".jpeg") || uri.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (uri.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (uri.toString().contains(".3gp") || uri.toString().contains(".mpg") || uri.toString().contains(".mpeg") || uri.toString().contains(".mpe") || uri.toString().contains(".mp4") || uri.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file
            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void openFile(Context context, File file, String fileMimeType) throws ActivityNotFoundException {
        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, fileMimeType);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
      /*  List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }*/
        context.startActivity(intent);
    }

    public static String getMimeType(String url) {
        String type = null;
        try {
            url = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static InternetType getInternetConnectionType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
//                Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
                return InternetType.WIFI;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
//                Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
                return InternetType.MOBILE;
            }
        } else {
            // not connected to the internet
            return InternetType.NOINTERNET;
        }
        return InternetType.NOINTERNET;
    }

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static long getUniqueIdForFile() {
        return System.currentTimeMillis();
    }

    public static boolean checkIfPartiallyDownloaded(String filePath, int fileLength) {
        File downloadedFile = new File(filePath);
        return (downloadedFile.exists() &&
                downloadedFile.length() < fileLength);
    }

    public static int getFileSize(URL url) {
        HttpURLConnection conn = null;
        HttpURLConnection conn1 = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.getInputStream();
            conn.connect();
            return conn.getContentLength();
        } catch (IOException e) {
            try {
                conn1 = (HttpURLConnection) url.openConnection();
                conn1.connect();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if (conn1 != null) {
                return conn1.getContentLength();
            }
            return -1;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (conn1 != null) {
                conn1.disconnect();
            }
        }
    }

}
