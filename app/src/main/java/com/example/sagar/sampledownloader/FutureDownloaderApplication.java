package com.example.sagar.sampledownloader;

import android.app.Application;

/**
 * Created by sagar on 7/4/17.
 */

public class FutureDownloaderApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
       /* if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);*/
        // Normal app init code...
    }

}
