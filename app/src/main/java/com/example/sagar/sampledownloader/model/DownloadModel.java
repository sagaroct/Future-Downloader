package com.example.sagar.sampledownloader.model;

/**
 * Created by sagar on 14/7/16.
 */
public class DownloadModel {

    private long mFileId;
    private String mTitle;
    private String mFileName;
    private int mDownloadPercentage;
    private boolean mIsHeader;
    private DownloadType mDownloadType;
    private int mFileSize;
    private long mDownloadDataProgress;
    private String mStrDate;
    private String mStrTime;
    /*private String mStrStopTime;
    private boolean mRepeatDownload;*/
    private String mFileUrl;
    private boolean mDownloadCompleted = true;

    public DownloadModel() {
    }

    public DownloadModel(String mTitle) {
        this.mFileId = 0;
        this.mTitle = mTitle;
        this.mFileName = "";
        this.mDownloadPercentage = 0;
        this.mIsHeader = true;
    }

    public DownloadModel(long fileId, String mFileName, String fileUrl, int fileSize, int mDownloadPercentage, DownloadType downloadType) {
        this.mFileId = fileId;
        this.mFileName = mFileName;
        this.mFileUrl = fileUrl;
        this.mFileSize = fileSize;
        this.mDownloadPercentage = mDownloadPercentage;
        this.mDownloadType = downloadType;
        this.mIsHeader = false;
    }

    //For future downloads
  /*  public DownloadModel(long fileId, String mFileName, String fileUrl, String strDate, String strTime, String strStopTime, boolean repeatDownload, DownloadType downloadType) {
        this.mFileId = fileId;
        this.mFileName = mFileName;
        this.mFileUrl = fileUrl;
        this.mStrDate = strDate;
        this.mStrTime = strTime;
        this.mStrStopTime = strStopTime;
        this.mRepeatDownload = repeatDownload;
        this.mDownloadType = downloadType;
        this.mIsHeader = false;
    }*/

    //For future downloads without stop feature
    public DownloadModel(long fileId, String mFileName, String fileUrl, String strDate, String strTime, DownloadType downloadType) {
        this.mFileId = fileId;
        this.mFileName = mFileName;
        this.mFileUrl = fileUrl;
        this.mStrDate = strDate;
        this.mStrTime = strTime;
        this.mDownloadType = downloadType;
        this.mIsHeader = false;
    }
    public boolean isDownloadCompleted() {
        return mDownloadCompleted;
    }

    public void setDownloadCompleted(boolean mDownloadCompleted) {
        this.mDownloadCompleted = mDownloadCompleted;
    }

    public String getFileUrl() {
        return mFileUrl;
    }

    public void setFileUrl(String mFileUrl) {
        this.mFileUrl = mFileUrl;
    }

    public DownloadType getDownloadType() {
        return mDownloadType;
    }

    public void setDownloadType(DownloadType mDownloadType) {
        this.mDownloadType = mDownloadType;
    }

    public long getFileId() {
        return mFileId;
    }

    public void setFileId(long mFileId) {
        this.mFileId = mFileId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String mDownloadContentName) {
        this.mFileName = mDownloadContentName;
    }

    public int getDownloadPercentage() {
        return mDownloadPercentage;
    }

    public void setDownloadPercentage(int mDownloadPercentage) {
        this.mDownloadPercentage = mDownloadPercentage;
    }

    public boolean isHeader() {
        return mIsHeader;
    }

    public void setIsHeader(boolean mIsHeader) {
        this.mIsHeader = mIsHeader;
    }

    public int getFileSize() {
        return mFileSize;
    }

    public void setFileSize(int mFileSize) {
        this.mFileSize = mFileSize;
    }

    public long getDownloadDataProgress() {
        return mDownloadDataProgress;
    }

    public void setDownloadDataProgress(long mDownloadDataProgress) {
        this.mDownloadDataProgress = mDownloadDataProgress;
    }

    public String getStrDate() {
        return mStrDate;
    }

    public void setStrDate(String mStrDate) {
        this.mStrDate = mStrDate;
    }

    public String getStrTime() {
        return mStrTime;
    }

    public void setStrTime(String mStrTime) {
        this.mStrTime = mStrTime;
    }

   /* public String getStrStopTime() {
        return mStrStopTime;
    }

    public void setStrStopTime(String mStrStopTime) {
        this.mStrStopTime = mStrStopTime;
    }

    public boolean isRepeatDownload() {
        return mRepeatDownload;
    }

    public void setRepeatDownload(boolean mRepeatDownload) {
        this.mRepeatDownload = mRepeatDownload;
    }*/
}
