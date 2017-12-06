package com.example.sagar.sampledownloader.service;
/**
 * Created by sagar on 21/7/16.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.URLUtil;

import com.example.sagar.sampledownloader.BuildConfig;
import com.example.sagar.sampledownloader.R;
import com.example.sagar.sampledownloader.common.CommonUtils;
import com.example.sagar.sampledownloader.common.Constants;
import com.example.sagar.sampledownloader.common.LogUtils;
import com.example.sagar.sampledownloader.helper.DownloadedFileStatusManager;
import com.example.sagar.sampledownloader.model.RunnableFuture;
import com.example.sagar.sampledownloader.view.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiDownloadService extends Service {

    private static final String TAG = MultiDownloadService.class.getSimpleName();

    private ExecutorService mExecutorService;
    // counter for number of files we have downloaded
    private int mDownloadsStarted;
    private int mDownloadsFinished;
    private Handler mHandler;
    private NotificationManager mNotificationManager;
    private DownloadedFileStatusManager mDownloadedFileStatusManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: called");
        // let's create a thread pool with five threads
        mExecutorService = Executors.newFixedThreadPool(5);
        mDownloadsStarted = 0;
        mDownloadsFinished = 0;
        mHandler = new Handler();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mDownloadedFileStatusManager = DownloadedFileStatusManager.getInstance();
    }

    private NotificationCompat.Builder getNotificationBuilder(String fileName) {
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this);
        mNotificationBuilder.setContentTitle(
                fileName)
                .setContentText(fileName)
                .setSmallIcon(R.mipmap.ic_launcher).setContentInfo("0%");
        return mNotificationBuilder;
    }

    protected void showNotification(NotificationCompat.Builder mNotificationBuilder, long fileId, String filePath, int notificationId, String fileName, String fileUrl, int progress, String progressData, long totalBytesRead, int fileSize) {
        Log.d(TAG, "showNotification: Progress: "+progress);
        if (progress < 100) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent, 0);
            mNotificationBuilder.setOngoing(true);
            mNotificationBuilder.setContentTitle(getString(R.string.download));
            mNotificationBuilder.setContentText(fileName);
            mNotificationBuilder.setProgress(100, progress,
                    false).setContentInfo(progressData).setContentIntent(pendingIntent);
            broadcastToShowProgressInUi(fileId, fileUrl, fileName, progress, totalBytesRead, fileSize);
            mNotificationManager.notify(notificationId, mNotificationBuilder.build());
        } else {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            File file = new File(filePath); // set your audio path
            intent.setDataAndType(FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file), CommonUtils.getMimeType(fileUrl));
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            mNotificationBuilder.setContentTitle(fileName);
            mNotificationBuilder.setContentText(getString(R.string.download_complete));
            mNotificationBuilder.setProgress(0, 0, false).setContentInfo("").setAutoCancel(true).setOngoing(false).setContentIntent(pendingIntent);
            broadcastToShowProgressInUi(fileId, fileUrl, fileName, progress, totalBytesRead, fileSize);
            Notification notification = mNotificationBuilder.build();
//            notification.flags |= Notification.FLAG_NO_CLEAR;
            mNotificationManager.notify(notificationId, notification);
        }
    }

    private void broadcastToShowProgressInUi(long fileId, String fileUrl, String fileName, int progress, long totalBytesRead, int fileSize) {
        try {
            Log.d(TAG, "broadcastToShowProgressInUi");
            Intent broadcastIntent = new Intent(Constants.DOWNLOAD_PROGRESS_UPDATE);
            broadcastIntent.putExtra(Constants.DOWNLOAD_PROGRESS, progress);
            broadcastIntent.putExtra(Constants.DOWNLOAD_DATA_PROGRESS, totalBytesRead);
            broadcastIntent.putExtra(Constants.FILE_ID, fileId);
            broadcastIntent.putExtra(Constants.FILE_NAME, fileName);
            broadcastIntent.putExtra(Constants.FILE_SIZE, fileSize);
            broadcastIntent.putExtra(Constants.FILE_URL, fileUrl);
            this.sendBroadcast(broadcastIntent);
        }
        catch (Exception e){
            Log.d(TAG, "broadcastToShowProgressInUi: exception:"+e.toString());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        if (intent != null) {
            final String action = intent.getAction();
            if (Constants.DOWNLOAD_PROGRESS_UPDATE.equals(action)) {
                // lets say that we are sending the url in the intent
                String url = intent.getStringExtra(Constants.DOWNLOAD_URL);
                long fileId = intent.getLongExtra(Constants.FILE_ID, 0);
                if (mDownloadedFileStatusManager.getCancellableFutures().containsKey(fileId) &&
                        mDownloadedFileStatusManager.getCancellableFutures().get(fileId).getDownloadRunnable().isCancelled()) {
                    mExecutorService.submit(mDownloadedFileStatusManager.getCancellableFutures().get(fileId).getDownloadRunnable());
                    mDownloadedFileStatusManager.resumeDownload(fileId);
                } else {
//                    mDownloadsStarted++;
                    String fileName;
                    if (mDownloadedFileStatusManager.getMapFilesDownloaded().containsKey(fileId)) {
                        fileName = mDownloadedFileStatusManager.getMapFilesDownloaded().get(fileId);
                    } else {
                        fileName = URLUtil.guessFileName(url, null, null);
                    }
                    // create a runnable for the ExecutorService
                    DownloadRunnable task = new DownloadRunnable(getApplicationContext(), url, fileId, CommonUtils.getUniqueIdForNotification(), getNotificationBuilder(fileName));
                    // submit it to the ExecutorService, this will be put on the queue and run using a thread
                    // from the ExecutorService pool
                    Future future = mExecutorService.submit(task);
                    RunnableFuture runnableFuture = new RunnableFuture();
                    runnableFuture.setDownloadRunnable(task);
                    runnableFuture.setFuture(future);
                    mDownloadedFileStatusManager.addFileToMap(fileId, fileName);
                    mDownloadedFileStatusManager.addCancelableRunnableFuture(fileId, runnableFuture);
                    mDownloadedFileStatusManager.resumeDownload(fileId);
                    mDownloadsStarted++;
                }
            }
        }
        // tells the OS to restart if we get killed after returning
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "in onBind");
        return null;
    }

    public class DownloadRunnable implements Runnable {

        private static final int BUFFER_SIZE = 4096;

        private String mUrlStr;
        private long mFileId;
        private int mUniqueNitificationId;
        private NotificationCompat.Builder mNotificationBuilder;
        private int mFileSize;
        private String mFileName;
        private String mFilePath;
        private boolean mIsCancelled;
        private boolean mIsDeleted;


        public DownloadRunnable(Context context, String url, long fileId, int uniqueId, NotificationCompat.Builder notificationBuilder) {
            this.mUrlStr = url;
            this.mFileId = fileId;
            this.mUniqueNitificationId = uniqueId;
            this.mNotificationBuilder = notificationBuilder;
            this.mFileName = URLUtil.guessFileName(mUrlStr, null, null);
            this.mFileName = CommonUtils.getFileName(context, mFileName);
                this.mFilePath = CommonUtils.getFilePath(context, mFileName);
        }

        public int getNotificationId() {
            return mUniqueNitificationId;
        }

        public boolean isCancelled() {
            return mIsCancelled;
        }

        public void setCancelled(boolean mIsCancelled) {
            this.mIsCancelled = mIsCancelled;
        }

        public void setDeleted(boolean deleted) {
            mIsDeleted = deleted;
        }

        @Override
        public void run() {
            Log.d(TAG, "run: ");
            URL url;
            HttpURLConnection httpConn = null;
            try {
                int loopCount = 0;
                int progressPercent;
                url = new URL(mUrlStr);
                httpConn = (HttpURLConnection) url.openConnection();
                mFileSize = CommonUtils.getFileSize(url);
                InputStream inputStream;
                FileOutputStream outputStream;
                boolean isPartialDownloaded = CommonUtils.checkIfPartiallyDownloaded(mFilePath, mFileSize);
                long partialFileLength = 0;
                if (isPartialDownloaded) {
                    File partialFile = new File(mFilePath);
                    partialFileLength = partialFile.length();
                    Log.d(TAG, "partialFileLength1: " + partialFile.length());
                    httpConn.setRequestProperty("Range", "bytes=" + (partialFile.length()) + "-");
                    inputStream = httpConn.getInputStream();
                    outputStream = new FileOutputStream(mFilePath, true);
                } else {
                    inputStream = httpConn.getInputStream();
                    outputStream = new FileOutputStream(mFilePath);
                }
                //StartForeground done to avoid stoppage of downloads when app is killed.
                startForeground(mUniqueNitificationId, mNotificationBuilder.build());
                int bytesRead = -1;
                long totalBytesRead = 0;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    if (!mIsCancelled) {
                        totalBytesRead += bytesRead;
                        outputStream.write(buffer, 0, bytesRead);
                        progressPercent = (int) ((totalBytesRead * 100) / mFileSize);
                        if (loopCount++ % 20 == 0 || progressPercent == 100) {
                            String kbytes = String.format("%s / %s", CommonUtils.getStringByteSize(totalBytesRead), CommonUtils.getStringByteSize(mFileSize));
                            if (isPartialDownloaded) {
                                totalBytesRead += partialFileLength;
                                progressPercent = (int) ((totalBytesRead * 100) / mFileSize);
                                isPartialDownloaded = false;
                            }
                            showNotification(mNotificationBuilder, mFileId, mFilePath, mUniqueNitificationId, mFileName, mUrlStr, progressPercent, kbytes, totalBytesRead, mFileSize);
                        }
                    }
                    else if(mIsDeleted){
                        try {
                            removeNotification(mFileId);
                        }
                        catch (NullPointerException nPE){
                            Log.e(TAG, "run: "+ nPE.toString() );
                        }
                        break;
                    }
                    else{
                        mDownloadsFinished--;
                        break;
                    }
                }
                Log.d(TAG, "totalBytesRead: " + totalBytesRead);
                File downloadedFile = new File(mFilePath);
                Log.d(TAG, "downloadedFileLength: " + downloadedFile.length());
                outputStream.close();
                inputStream.close();
            }
            /*catch (InterruptedException iE){
                LogUtils.logE(TAG, "InterruptedException: "+ iE.toString());
            }*/ catch (CancellationException cE) {
                DownloadedFileStatusManager.getInstance().stopDownload(mFileId);
                mDownloadsFinished--;
                LogUtils.logE(TAG, "CancellationException: " + cE.toString());
            } catch (Exception e) {
                DownloadedFileStatusManager.getInstance().stopDownload(mFileId);
                mDownloadsFinished--;
                Log.e("MultiThreadedService", e.getMessage(), e);
            } finally {
                if (httpConn != null) {
                    httpConn.disconnect();
//                    stopForeground(true);
                }
            }
            mDownloadsFinished++;
            finished();
        }
    }

    /**
     * to be called by the dl task.  if we have exhausted our list of dl's let's shutdown.
     */
    private void finished() {
        Log.d(TAG, "mDownloadsFinished: "+mDownloadsFinished + " mDownloadsStarted: "+mDownloadsStarted);
        if (mDownloadsFinished == mDownloadsStarted) {
            Log.d(TAG, "mDownloadsFinished == mDownloadsStarted ");
            stopForeground(true);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "downloaded " + mDownloadsFinished + " files, shutting down.");
                    stopSelf();
                }
            });
        }
    }

    private void removeNotification(long fileId) throws NullPointerException{
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int notificationId = DownloadedFileStatusManager.getInstance().getCancellableFutures().get(fileId).getDownloadRunnable().getNotificationId();
        stopForeground(true);
        notificationManager.cancel(notificationId);
    }
}
