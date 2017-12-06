package com.example.sagar.sampledownloader.model;

/**
 * Created by sagar on 1/8/16.
 */
public class SettingsModel {

    private String mTitle;
    private String mPath;
    private boolean isWifiChecked;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public boolean isWifiChecked() {
        return isWifiChecked;
    }

    public void setWifiChecked(boolean wifiChecked) {
        isWifiChecked = wifiChecked;
    }
}
