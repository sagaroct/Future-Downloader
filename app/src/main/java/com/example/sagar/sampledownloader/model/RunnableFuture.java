package com.example.sagar.sampledownloader.model;

import com.example.sagar.sampledownloader.service.MultiDownloadService;

import java.util.concurrent.Future;

/**
 * Created by sagar on 30/8/16.
 */
public class RunnableFuture {

    private MultiDownloadService.DownloadRunnable mDownloadRunnable;
    private Future mFuture;

    public MultiDownloadService.DownloadRunnable getDownloadRunnable() {
        return mDownloadRunnable;
    }

    public void setDownloadRunnable(MultiDownloadService.DownloadRunnable mDownloadRunnable) {
        this.mDownloadRunnable = mDownloadRunnable;
    }

    public Future getFuture() {
        return mFuture;
    }

    public void setFuture(Future mFuture) {
        this.mFuture = mFuture;
    }
}
