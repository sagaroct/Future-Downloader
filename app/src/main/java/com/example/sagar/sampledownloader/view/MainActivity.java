package com.example.sagar.sampledownloader.view;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.sagar.sampledownloader.R;
import com.example.sagar.sampledownloader.adapter.ExpandableListAdapter;
import com.example.sagar.sampledownloader.common.AlarmanagerUtils;
import com.example.sagar.sampledownloader.common.CommonUtils;
import com.example.sagar.sampledownloader.common.Constants;
import com.example.sagar.sampledownloader.common.LogUtils;
import com.example.sagar.sampledownloader.common.PermissionUtils;
import com.example.sagar.sampledownloader.helper.DbHelper;
import com.example.sagar.sampledownloader.helper.DialogHelper;
import com.example.sagar.sampledownloader.helper.DownloadedFileStatusManager;
import com.example.sagar.sampledownloader.model.DownloadModel;
import com.example.sagar.sampledownloader.model.DownloadType;
import com.example.sagar.sampledownloader.service.MultiDownloadService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ExpandableListView.OnChildClickListener, AdapterView.OnItemLongClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ExpandableListAdapter mDownloadAdapter;
    private DbHelper mDbHelper;
    private ExpandableListView mExpListView;
    private List<String> mListDataHeader;
    private HashMap<String, List<DownloadModel>> mListDataChild;
    private List<DownloadModel> mCurrentDownloadList;
    private List<DownloadModel> mFutureDownloadList;
    private List<DownloadModel> mPastDownloadList;
//    private AdView mAdViewBanner;
    private FrameLayout mFLEmptyDownload;
    private View mNewDownloadDialogLayout;
//    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mExpListView = (ExpandableListView) findViewById(R.id.rv_home);
        mFLEmptyDownload = (FrameLayout) findViewById(R.id.fl_empty_download);
        mDbHelper = DbHelper.getInstance(this);
        mListDataChild = getDownloadList();
        mListDataHeader = new ArrayList<String>(mListDataChild.keySet());
        mDownloadAdapter = new ExpandableListAdapter(mListDataHeader, mListDataChild);
        mExpListView.setAdapter(mDownloadAdapter);
        mExpListView.setOnChildClickListener(this);
        mExpListView.setOnItemLongClickListener(this);
//        initializeAds();
        Log.d(TAG, "onCreate called");
        onNewIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent called: " + intent.getData());
        super.onNewIntent(intent);
        String action = intent.getAction();
        //When an download url is tapped from any browser
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri data = intent.getData();
            showDialogToEnterUrlAndDownload(data.toString());
        }
       /* else if(action.equals(Constants.DOWNLOAD_COMPLETED)) {
            String path = intent.getStringExtra(Constants.FILE_PATH);
            String fileUrl = intent.getStringExtra(Constants.FILE_URL);
            CommonUtils.openFile(MainActivity.this, Uri.parse("file:/" + path), CommonUtils.getMimeType(fileUrl));
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_add_url:
                showDialogToEnterUrlAndDownload(null);
                return true;
            case R.id.mi_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            case R.id.mi_help:
                DialogHelper.showDialogToDisplayText(MainActivity.this, getString(R.string.help), getString(R.string.help_content));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        if (CommonUtils.isConnectedToInternet(this)) {
            findViewById(R.id.network_connectivity_indicator_text).setVisibility(View.GONE);
        } else {
            findViewById(R.id.network_connectivity_indicator_text).setVisibility(View.VISIBLE);
        }
        IntentFilter intentFilter = new IntentFilter(Constants.DOWNLOAD_PROGRESS_UPDATE);
        registerReceiver(mDownloadProgressReceiver, intentFilter);
        /*if (mAdViewBanner != null) {
            mAdViewBanner.resume();
        }*/
        super.onResume();
    }

    @Override
    public void onPause() {
        LogUtils.logD(TAG, "onPause");
       /* if (mAdViewBanner != null) {
            mAdViewBanner.pause();
        }*/
        super.onPause();
    }

    @Override
    public void onDestroy() {
        LogUtils.logD(TAG, "onDestroy");
        if (mDownloadProgressReceiver != null) {
            unregisterReceiver(mDownloadProgressReceiver);
        }
      /*  if (mAdViewBanner != null) {
            mAdViewBanner.destroy();
        }*/
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case PermissionUtils.PERMISSION_REQUEST_CODE:if(grantResults.length>0){
               if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                   mNewDownloadDialogLayout.findViewById(R.id.btn_ok).callOnClick();
               }
            }

        }
    }

    /*private void initializeAds() {
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        mAdViewBanner = (AdView) findViewById(R.id.ad_view_banner);
        AdRequest adRequest = new AdRequest.Builder()*//*.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)*//*
                .build();
        mAdViewBanner.loadAd(adRequest);
        mInterstitialAd  = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstital_ad_unit_id));
        loadInterstitialAd();
    }*/

/*    private void loadInterstitialAd() {
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
        }
    }

    private void showInterstitial() {
        // Show the ad if it's ready.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }*/


    private void showDialogToEnterUrlAndDownload(final String strUrl) {
        mNewDownloadDialogLayout = getLayoutInflater().inflate(R.layout.dialog_add_new_download, null);
        DialogHelper dialogHelper = new DialogHelper(MainActivity.this, mNewDownloadDialogLayout);
        final AlertDialog alertDialog = dialogHelper.create();
        final Button btnStartDate = (Button) mNewDownloadDialogLayout.findViewById(R.id.btn_start_date);
        final Button btnStartTime = (Button) mNewDownloadDialogLayout.findViewById(R.id.btn_start_time);
        final Button btnStopTime = (Button) mNewDownloadDialogLayout.findViewById(R.id.btn_stop_time);
        final CheckBox cbRepeat = (CheckBox) mNewDownloadDialogLayout.findViewById(R.id.cb_repeat);
        final EditText edtUrl = (EditText) mNewDownloadDialogLayout.findViewById(R.id.edt_url);
        final Button btnOk = (Button) mNewDownloadDialogLayout.findViewById(R.id.btn_ok);
        if (!TextUtils.isEmpty(strUrl)) {
            edtUrl.setText(strUrl);
        }
        mNewDownloadDialogLayout.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String urlStr = edtUrl.getText().toString();
                if (!TextUtils.isEmpty(urlStr) && URLUtil.isValidUrl(urlStr)) {
                    if(PermissionUtils.checkPermission(MainActivity.this)) {
                        String fileName = URLUtil.guessFileName(urlStr, null, null);
                        fileName = CommonUtils.getFileName(MainActivity.this, fileName);
                        boolean isStarted = ValidateAndStartDownload(urlStr, fileName, CommonUtils.getUniqueIdForFile(), btnStartDate.getText().toString(), btnStartTime.getText().toString(),
                                btnStopTime.getText().toString(), cbRepeat.isChecked());
                        if (isStarted) {
                            alertDialog.dismiss();
                        }
                    }else{
                        PermissionUtils.requestPermission(MainActivity.this);
                    }
                } else {
                    edtUrl.setError(getString(R.string.warning_url));
//                    Toast.makeText(MainActivity.this, getString(R.string.warning_url), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogHelper.showDatePickerDialog(MainActivity.this, btnStartDate, btnStartTime, btnOk);
            }
        });
        btnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogHelper.showTimePickerDialog(MainActivity.this, btnStartTime, btnOk);
            }
        });
        btnStopTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogHelper.showStopTimePickerDialog(MainActivity.this, btnStopTime, cbRepeat);
            }
        });
        alertDialog.show();
    }

    private boolean ValidateAndStartDownload(final String urlStr, final String fileName, final long fileId, final String startDate,
                                             final String startTime, final String stopTime, final boolean repeatDownload) {
        if (!startDate.equalsIgnoreCase(getString(R.string.start_date)) && !startTime.equalsIgnoreCase(getString(R.string.start_time))) {
            final long dateTimeMillis = CommonUtils.getMillisecondsFromDateTime(startDate, startTime);
            if (dateTimeMillis != 0) {
                if (PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean(Constants.WIFI_PREFERENCE, false)) {
                    switch (CommonUtils.getInternetConnectionType(MainActivity.this)) {
                        case WIFI:
                            addAndStartFutureDownload(urlStr, fileName, fileId, startDate, startTime, stopTime, repeatDownload, dateTimeMillis);
                            break;
                        case MOBILE:
                            DialogHelper.showDialogToDownloadOverMobileInternet(MainActivity.this, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int pos) {
                                    addAndStartFutureDownload(urlStr, fileName, fileId, startDate, startTime, stopTime, repeatDownload, dateTimeMillis);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int pos) {
                                    dialogInterface.dismiss();
                                }
                            });
                    }
                } else{
                    addAndStartFutureDownload(urlStr, fileName, fileId, startDate, startTime, stopTime, repeatDownload, dateTimeMillis);
                }
            } else
                Toast.makeText(MainActivity.this, R.string.date_time_error, Toast.LENGTH_SHORT).show();

        } else {
            //If download over wifi only is set true in settings
            if (PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean(Constants.WIFI_PREFERENCE, false)) {
                switch (CommonUtils.getInternetConnectionType(MainActivity.this)) {
                    case WIFI:
                        addAndStartDownload(urlStr, fileName, fileId);
                        break;
                    case MOBILE:
                        DialogHelper.showDialogToDownloadOverMobileInternet(MainActivity.this, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                addAndStartDownload(urlStr, fileName, fileId);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                dialogInterface.dismiss();
                            }
                        });
                }
            } else if (CommonUtils.isConnectedToInternet(MainActivity.this)) {
                addAndStartDownload(urlStr, fileName, fileId);
            } else {
                return false;
            }
        }
        return true;
    }

    private void addAndStartFutureDownload(String urlStr, String fileName, long fileId, String strDate, String strTime, String stopTime,
                                           boolean repeatDownload, long dateTimeMillis) {
//        loadInterstitialAd();
        hideEmptyDownloadView();
        mDbHelper.addFutureDownload(fileId, fileName, urlStr, strDate, strTime);
        mFutureDownloadList.add(new DownloadModel(fileId, fileName, urlStr, strDate, strTime, DownloadType.FUTURE));
        mDownloadAdapter.notifyDataSetChanged();
        AlarmanagerUtils.setDownloadTimer(MainActivity.this, urlStr, fileId, dateTimeMillis);

    }

    private void addAndStartDownload(String urlStr, String fileName, long fileId) {
//        loadInterstitialAd();
        hideEmptyDownloadView();
        mCurrentDownloadList.add(new DownloadModel(fileId, fileName, urlStr, 0, 0, DownloadType.CURRENT));
        mDownloadAdapter.notifyDataSetChanged();
        //after adding to list added to database
        mDbHelper.addCurrentDownload(fileId, fileName, 0, 0, 0);
        Intent downloadIntent = new Intent(MainActivity.this, MultiDownloadService.class);
        downloadIntent.setAction(Constants.DOWNLOAD_PROGRESS_UPDATE);
        downloadIntent.putExtra(Constants.DOWNLOAD_URL, urlStr);
        downloadIntent.putExtra(Constants.FILE_ID, fileId);
        startService(downloadIntent);
//        bindService(downloadIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void showDialogToChangeDateTimeOfDownload(final long fileId, String strUrl, String strDate, String strTime) {
        final View dialoglayout = getLayoutInflater().inflate(R.layout.dialog_add_new_download, null);
        DialogHelper dialogHelper = new DialogHelper(MainActivity.this, dialoglayout);
        final AlertDialog alertDialog = dialogHelper.create();
        final Button btnDate = (Button) dialoglayout.findViewById(R.id.btn_start_date);
        final Button btnTime = (Button) dialoglayout.findViewById(R.id.btn_start_time);
        final EditText edtUrl = (EditText) dialoglayout.findViewById(R.id.edt_url);
        final Button btnOk = (Button) dialoglayout.findViewById(R.id.btn_ok);
        edtUrl.setText(strUrl);
        edtUrl.setEnabled(false);
        btnDate.setText(strDate);
        btnTime.setText(strTime);
        btnTime.setEnabled(true);
        dialoglayout.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlStr = edtUrl.getText().toString();
                if (!TextUtils.isEmpty(urlStr)) {
                    String strDate = btnDate.getText().toString();
                    String strTime = btnTime.getText().toString();
                    validateAndUpdateDownload(urlStr, strDate, strTime, fileId);
                    alertDialog.dismiss();
                }
            }
        });
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogHelper.showDatePickerDialog(MainActivity.this, btnDate, null, null);
            }
        });
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogHelper.showTimePickerDialog(MainActivity.this, btnTime, btnOk);
            }
        });
        alertDialog.show();
    }


    private void validateAndUpdateDownload(String urlStr, String strDate, String strTime, long fileId) {
//        loadInterstitialAd();
        if (!strDate.equalsIgnoreCase(getString(R.string.start_date)) && !strTime.equalsIgnoreCase(getString(R.string.start_time))) {
            long dateTimeMillis = CommonUtils.getMillisecondsFromDateTime(strDate, strTime);
            if (dateTimeMillis != 0) {
                mDbHelper.updateFutureDownload(fileId, strDate, strTime);
                DownloadModel downloadModel = getFutureDownloadById(fileId);
                if (downloadModel != null) {
                    downloadModel.setStrDate(strDate);
                    downloadModel.setStrTime(strTime);
                    mDownloadAdapter.notifyDataSetChanged();
                    AlarmanagerUtils.cancelDownloadFromAlarmManager(MainActivity.this, fileId, urlStr);
                    AlarmanagerUtils.setDownloadTimer(MainActivity.this, urlStr, fileId, dateTimeMillis);
                }
            } else {
                Toast.makeText(MainActivity.this, R.string.date_time_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private BroadcastReceiver mDownloadProgressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "mDownloadProgressReceiver onReceive: ");
            int progressPercent = intent.getIntExtra(Constants.DOWNLOAD_PROGRESS, 0);
            long progressData = intent.getLongExtra(Constants.DOWNLOAD_DATA_PROGRESS, 0);
            long fileId = intent.getLongExtra(Constants.FILE_ID, 0);
            String fileName = intent.getStringExtra(Constants.FILE_NAME);
            int fileSize = intent.getIntExtra(Constants.FILE_SIZE, 0);
            String fileUrl = intent.getStringExtra(Constants.FILE_URL);
            LogUtils.logD(TAG, "mDownloadProgressReceiver : received" + "\n Progress Percentage:" + progressPercent);
            if (mFutureDownloadList.remove(getFutureDownloadById(fileId))) {
                mDownloadAdapter.notifyDataSetChanged();
            }
            handleDownloadProgress(progressPercent, progressData, fileId, fileName, fileSize, fileUrl);
        }
    };

    private void handleDownloadProgress(int progressPercent, long progressData, long fileId, String fileName, int fileSize, String fileUrl) {
        if (mCurrentDownloadList.size() == 0) {
            DownloadModel localDownload = new DownloadModel();
            localDownload.setFileName(fileName);
            localDownload.setFileId(fileId);
            localDownload.setDownloadPercentage(progressPercent);
            localDownload.setDownloadDataProgress(progressData);
            localDownload.setDownloadType(DownloadType.CURRENT);
            localDownload.setFileSize(fileSize);
            localDownload.setFileUrl(fileUrl);
            mCurrentDownloadList.add(localDownload);
            mDownloadAdapter.notifyDataSetChanged();
        } else {
            boolean notIncurrentDownload = true;
            for (int i = 0; i < mCurrentDownloadList.size(); i++) {
               /* Log.d(TAG, "CurrentDownloadModelListSize: " + mCurrentDownloadList.size());
                Log.d(TAG, "CurrentDownloadModelList ith pos: " + i);*/
                DownloadModel currentDownload = getCurrentDownloadById(fileId);
                if (currentDownload != null) {
                    currentDownload.setDownloadPercentage(progressPercent);
                    currentDownload.setDownloadDataProgress(progressData);
                    currentDownload.setFileSize(fileSize);
                    mDownloadAdapter.notifyDataSetChanged();
                    updateDatabase(fileId, progressData, progressPercent);
                    if (progressPercent == 100) {
//                        showInterstitial();
                        mPastDownloadList.add(currentDownload);
                        mCurrentDownloadList.remove(currentDownload);
                        mDownloadAdapter.notifyDataSetChanged();
                        mDbHelper.deleteCurrentDownload(fileId);
                        mDbHelper.addPastDownload(currentDownload);
                    }
                    notIncurrentDownload = false;
                }
            }
            if (notIncurrentDownload) {
                for (DownloadModel downloadModel : mFutureDownloadList) {
                    if (downloadModel.getFileId() == fileId) {
                        return;
                    }
                }
                DownloadModel localDownload = new DownloadModel();
                localDownload.setFileName(fileName);
                localDownload.setFileId(fileId);
                localDownload.setDownloadPercentage(progressPercent);
                localDownload.setDownloadDataProgress(progressData);
                localDownload.setDownloadType(DownloadType.CURRENT);
                localDownload.setFileSize(fileSize);
                localDownload.setFileUrl(fileUrl);
                mCurrentDownloadList.add(localDownload);
                mDownloadAdapter.notifyDataSetChanged();
            }
        }
    }

    private HashMap<String, List<DownloadModel>> getDownloadList() {
        LinkedHashMap<String, List<DownloadModel>> parentListItems = new LinkedHashMap<>();
        mCurrentDownloadList = mDbHelper.getAllCurrentDownloads();
        mFutureDownloadList = mDbHelper.getAllFutureDownloads();
        mPastDownloadList = mDbHelper.getAllPastDownloads();
        parentListItems.put(getString(R.string.current_downloads), mCurrentDownloadList);
        parentListItems.put(getString(R.string.future_downloads), mFutureDownloadList);
        parentListItems.put(getString(R.string.past_downloads), mPastDownloadList);
        if (mCurrentDownloadList.size() == 0 && mFutureDownloadList.size() == 0 && mPastDownloadList.size() == 0) {
            showEmptyDownloadView();
        } else {
            hideEmptyDownloadView();
        }
        return parentListItems;
    }

    private void showEmptyDownloadView() {
        mFLEmptyDownload.setVisibility(View.VISIBLE);
        mExpListView.setVisibility(View.GONE);
    }

    private void hideEmptyDownloadView() {
        if (mFLEmptyDownload.isShown()) {
            mExpListView.setVisibility(View.VISIBLE);
            mFLEmptyDownload.setVisibility(View.GONE);
        }
    }

    private void updateDatabase(final long fileId, final long progressData, final int progressPercent) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                mDbHelper.updateCurrentDownloadProgress(fileId, progressData, progressPercent);
            }
        });
        t.start();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        switch (groupPosition) {
            case 0:
                try {
                    DialogHelper.showDialogToDisplayText(MainActivity.this, getString(R.string.file_name), mCurrentDownloadList.get(childPosition).getFileName());
                } catch (IndexOutOfBoundsException iOOB) {
                    Log.e(TAG, "onChildClick: mCurrentDownloadList " + iOOB);
                }
                return true;
            case 1:
                try {
                    DialogHelper.showDialogToDisplayText(MainActivity.this, getString(R.string.file_name), mFutureDownloadList.get(childPosition).getFileName());
                } catch (IndexOutOfBoundsException iOOB) {
                    Log.e(TAG, "onChildClick: mFutureDownloadList " + iOOB);
                }
                return true;
            case 2:
                try {
                    File file = new File(CommonUtils.getFilePathWithoutFileNameChanged(MainActivity.this, mPastDownloadList.get(childPosition).getFileName()));
                    CommonUtils.openFile(MainActivity.this, file,
                            CommonUtils.getMimeType(mPastDownloadList.get(childPosition).getFileUrl()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
        }
        return false;
    }

    private void handleCurrentDownloadLongClick(final int childPosition) {
        final DownloadModel currentDownload = mCurrentDownloadList.get(childPosition);
        DialogHelper.showDialogToDeleteRemoveCancelFileDownload(MainActivity.this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int pos) {
                long fileId = currentDownload.getFileId();
                String fileName = currentDownload.getFileName();
                switch (pos) {
                    //Uncomment when pause feature is to be added
                    /*case 0:
                        if (CommonUtils.isConnectedToInternet(MainActivity.this)) {
                            if (DownloadedFileStatusManager.getInstance().isPaused(currentDownload.getFileId())) {
                                Intent downloadIntent = new Intent(MainActivity.this, MultiDownloadService.class);
                                downloadIntent.setAction(Constants.DOWNLOAD_PROGRESS_UPDATE);
                                downloadIntent.putExtra(Constants.DOWNLOAD_URL, currentDownload.getFileUrl());
                                downloadIntent.putExtra(Constants.FILE_ID, fileId);
                                startService(downloadIntent);
                            } else {
                                DownloadedFileStatusManager.getInstance().stopDownload(fileId);
                                Toast.makeText(MainActivity.this, "Paused download of: " + fileName, Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;*/
                    case 0:
                        DownloadedFileStatusManager.getInstance().deleteDownload(fileId);
                        mDbHelper.deleteCurrentDownload(mCurrentDownloadList.get(childPosition).getFileId());
                        removeCurrentDownload(fileId);
                        /*try {
                            removeNotification(fileId);
                        }
                        catch (NullPointerException nPE){
                            Log.e(TAG, "deleteCurrentDownload: "+nPE.toString() );
                        }*/
                        Toast.makeText(MainActivity.this, getString(R.string.removed) + fileName, Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        DownloadedFileStatusManager.getInstance().deleteDownload(fileId);
                        mDbHelper.deleteCurrentDownload(mCurrentDownloadList.get(childPosition).getFileId());
                        File file = new File(CommonUtils.getFilePathWithoutFileNameChanged(MainActivity.this, mCurrentDownloadList.get(childPosition).getFileName()));
                        file.delete();
                        removeCurrentDownload(fileId);
                       /* try {
                            removeNotification(fileId);
                        }
                        catch (NullPointerException nPE){
                            Log.e(TAG, "deleteCurrentDownload: "+nPE.toString() );
                        }*/
                        Toast.makeText(MainActivity.this, getString(R.string.remove_and_deleted) + fileName, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, DownloadedFileStatusManager.getInstance().isPaused(currentDownload.getFileId()));
    }

    private void handleFutureDownloadLongClick(final int childPosition) {
        DialogHelper.showDialogToEditRemove(MainActivity.this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int pos) {
                DownloadModel downloadModel = mFutureDownloadList.get(childPosition);
                long fileId = downloadModel.getFileId();
                String url = downloadModel.getFileUrl();
                switch (pos) {
                    case 0:
                        showDialogToChangeDateTimeOfDownload(fileId, url, downloadModel.getStrDate(), downloadModel.getStrTime());
                        break;
                    case 1:
                        mDbHelper.deleteFutureDownload(fileId);
                        removeFutureDownload(fileId);
                        AlarmanagerUtils.cancelDownloadFromAlarmManager(MainActivity.this, fileId, url);
                        break;
                }
            }
        });
    }

    private void handlePastDownloadLongClick(final int childPosition) {
        DialogHelper.showDialogToDeleteOrRemoveFile(MainActivity.this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int pos) {
                long fileId = mPastDownloadList.get(childPosition).getFileId();
                String fileName = mPastDownloadList.get(childPosition).getFileName();
                switch (pos) {
                    case 0:
                        mDbHelper.deletePastDownload(mPastDownloadList.get(childPosition).getFileId());
                        removePastDownload(fileId);
                        Toast.makeText(MainActivity.this, getString(R.string.removed) + fileName, Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        mDbHelper.deletePastDownload(mPastDownloadList.get(childPosition).getFileId());
                        File file = new File(CommonUtils.getFilePath(MainActivity.this, mPastDownloadList.get(childPosition).getFileName()));
                        file.delete();
                        removePastDownload(fileId);
                        Toast.makeText(MainActivity.this, getString(R.string.remove_and_deleted) + fileName, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void removeNotification(long fileId) throws NullPointerException {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int notificationId = DownloadedFileStatusManager.getInstance().getCancellableFutures().get(fileId).getDownloadRunnable().getNotificationId();
        new MultiDownloadService().stopForeground(true);
        notificationManager.cancel(notificationId);
    }

    /**
     * Removes data item from mCurrentDownloadList
     *
     * @param fileId
     */
    private void removeCurrentDownload(long fileId) {
        mCurrentDownloadList.remove(getCurrentDownloadById(fileId));
        if (mCurrentDownloadList.size() == 0 && mFutureDownloadList.size() == 0 && mPastDownloadList.size() == 0) {
            showEmptyDownloadView();
        } else {
            mDownloadAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Removes data item from mPastDownloadList
     *
     * @param fileId
     */
    private void removePastDownload(long fileId) {
        mPastDownloadList.remove(getPastDownloadById(fileId));
        if (mCurrentDownloadList.size() == 0 && mFutureDownloadList.size() == 0 && mPastDownloadList.size() == 0) {
            showEmptyDownloadView();
        } else {
            mDownloadAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Removes data item from mFutureDownloadList
     *
     * @param fileId
     */
    private void removeFutureDownload(long fileId) {
        mFutureDownloadList.remove(getFutureDownloadById(fileId));
        if (mCurrentDownloadList.size() == 0 && mFutureDownloadList.size() == 0 && mPastDownloadList.size() == 0) {
            showEmptyDownloadView();
        } else {
            mDownloadAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Get current download corresponding to fileId
     *
     * @param fileId
     * @return
     */
    private DownloadModel getCurrentDownloadById(long fileId) {
        for (int i = 0; i < mCurrentDownloadList.size(); i++) {
            DownloadModel currentDownload = mCurrentDownloadList.get(i);
            if (currentDownload.getFileId() == fileId) {
                return currentDownload;
            }
        }
        return null;
    }

    /**
     * Get future download corresponding to fileId
     *
     * @param fileId
     * @return
     */
    private DownloadModel getFutureDownloadById(long fileId) {
        for (int i = 0; i < mFutureDownloadList.size(); i++) {
            DownloadModel futureDownload = mFutureDownloadList.get(i);
            if (futureDownload.getFileId() == fileId) {
                return futureDownload;
            }
        }
        return null;
    }

    /**
     * Get past download corresponding to fileId
     *
     * @param fileId
     * @return
     */
    private DownloadModel getPastDownloadById(long fileId) {
        for (int i = 0; i < mPastDownloadList.size(); i++) {
            DownloadModel pastDownload = mPastDownloadList.get(i);
            if (pastDownload.getFileId() == fileId) {
                return pastDownload;
            }
        }
        return null;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int groupPosition = ExpandableListView.getPackedPositionGroup(id);
            final int childPosition = ExpandableListView.getPackedPositionChild(id);
           try {
               switch (groupPosition) {
                   case 0:
                       handleCurrentDownloadLongClick(childPosition);
                       break;
                   case 1:
                       handleFutureDownloadLongClick(childPosition);
                       break;
                   case 2:
                       handlePastDownloadLongClick(childPosition);
                       break;
               }
           }catch (IndexOutOfBoundsException iOOB){
               Log.e(TAG, "onItemLongClick: "+ iOOB);
           }
            return true;
        }
        return false;
    }

}
