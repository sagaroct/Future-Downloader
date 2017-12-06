package com.example.sagar.sampledownloader.helper;

import com.example.sagar.sampledownloader.model.RunnableFuture;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sagar on 29/8/16.
 */
public class DownloadedFileStatusManager {

    private static DownloadedFileStatusManager mDownloadedFileStatusManager;
    private HashMap<Long, String> mMapFilesDownloaded = new HashMap<>();
//    private HashMap<Long, MultiDownloadService.DownloadRunnable> mMapDownloadThreads = new HashMap<>();

    private Map<Long, RunnableFuture> mCancellableFutures = new HashMap<>();
   /* Future<?> future = executor.submit(runnable);
    mCancellableFutures.put(runnable, future);*/

    public synchronized static DownloadedFileStatusManager getInstance() {
        if (mDownloadedFileStatusManager == null) {
            mDownloadedFileStatusManager = new DownloadedFileStatusManager();
        }
        return mDownloadedFileStatusManager;
    }

    public HashMap<Long, String> getMapFilesDownloaded() {
        return mMapFilesDownloaded;
    }

    public void addFileToMap(long fileId, String fileName) {
        mMapFilesDownloaded.put(fileId, fileName);
    }

   /* public void changeFileStatusAsDownloaded(long fileId){
        mMapFilesDownloaded.put(fileId, true);
    }*/

    /*public HashMap<Long, MultiDownloadService.DownloadRunnable> getMapDownloadThreads() {
        return mMapDownloadThreads;
    }*/

   /* public void addThreadToMap(long fileId, MultiDownloadService.DownloadRunnable downloadRunnable){
        mMapDownloadThreads.put(fileId, downloadRunnable);
    }*/

    public void addCancelableRunnableFuture(long fileId, RunnableFuture runnableFuture) {
        mCancellableFutures.put(fileId, runnableFuture);
    }

    public boolean stopDownload(long fileId) {
        if (mCancellableFutures.containsKey(fileId)) {
//            mMapDownloadThreads.get(fileId).interrupt();
            //now you want to abruptly cancel a particular task
            if (!mCancellableFutures.get(fileId).getDownloadRunnable().isCancelled()) {
                mCancellableFutures.get(fileId).getDownloadRunnable().setCancelled(true);
                mCancellableFutures.get(fileId).getFuture().cancel(true);
                return true;
            }
        }
        return false;
    }

    public boolean deleteDownload(long fileId) {
        if (mCancellableFutures.containsKey(fileId)) {
            mCancellableFutures.get(fileId).getDownloadRunnable().setCancelled(true);
            mCancellableFutures.get(fileId).getDownloadRunnable().setDeleted(true);
            mCancellableFutures.get(fileId).getFuture().cancel(true);
            return true;

        }
        return false;
    }

    public boolean isPaused(long fileId) {
        try {
            return mCancellableFutures.get(fileId).getDownloadRunnable().isCancelled();
        } catch (NullPointerException nPE) {
            //TODO: Dont know the logic currently.
            return false;
        }
    }

    public boolean resumeDownload(long fileId) {
        if (mCancellableFutures.containsKey(fileId)) {
//            mMapDownloadThreads.get(fileId).interrupt();
            //now you want to abruptly cancel a particular task
            if (mCancellableFutures.get(fileId).getDownloadRunnable().isCancelled()) {
                mCancellableFutures.get(fileId).getDownloadRunnable().setCancelled(false);
                mCancellableFutures.get(fileId).getFuture().cancel(false);
                return true;
            }
        }
        return false;
    }

    public Map<Long, RunnableFuture> getCancellableFutures() {
        return mCancellableFutures;
    }
}
