package com.example.sagar.sampledownloader.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.sagar.sampledownloader.R;
import com.example.sagar.sampledownloader.adapter.SettingsAdapter;
import com.example.sagar.sampledownloader.common.CommonUtils;
import com.example.sagar.sampledownloader.common.Constants;
import com.example.sagar.sampledownloader.model.SettingsModel;

import net.rdrei.android.dirchooser.DirectoryChooserConfig;
import net.rdrei.android.dirchooser.DirectoryChooserFragment;

import java.util.ArrayList;

/**
 * Created by sagar on 1/8/16.
 */
public class SettingsActivity extends AppCompatActivity implements SettingsAdapter.OnDirectoryPathChangeClickListener,
        DirectoryChooserFragment.OnFragmentInteractionListener{

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private SharedPreferences mSharedPreferences;
    private  Button mBtnPath;
    private DirectoryChooserFragment mDialog;
    private ArrayList<SettingsModel> mSettingModels;
    private SettingsAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getString(R.string.settings));
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        RecyclerView mRecyclerViewSettings = (RecyclerView) findViewById(R.id.rv_settings);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerViewSettings.setLayoutManager(mLayoutManager);
        mSettingModels = getSettingsModels();
        mAdapter = new SettingsAdapter(this, mSettingModels);
        mAdapter.setOnDirectoryPathChangeClickListener(this);
        mRecyclerViewSettings.setAdapter(mAdapter);
        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("DialogSample")
                .build();
        mDialog = DirectoryChooserFragment.newInstance(config);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        catch (NullPointerException nPE){
            nPE.printStackTrace();
        }
    }

    private ArrayList<SettingsModel> getSettingsModels() {
        ArrayList<SettingsModel> settingsModels = new ArrayList<>();
        SettingsModel settingsModel = new SettingsModel();
        settingsModel.setPath("");
        settingsModel.setTitle(getString(R.string.download_over_wifi));
        settingsModel.setWifiChecked(mSharedPreferences.getBoolean(Constants.WIFI_PREFERENCE, false));
        settingsModels.add(settingsModel);
        settingsModel = new SettingsModel();
        settingsModel.setTitle(getString(R.string.change_download_directory));
        settingsModel.setPath(CommonUtils.getFileDirectory(this));
        settingsModel.setWifiChecked(false);
        settingsModels.add(settingsModel);
        return settingsModels;
    }

    private void showDilaogToChangeDownloadPath(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(true);
        final View dialoglayout = getLayoutInflater().inflate(R.layout.dialog_change_filepath, null);
        alertDialogBuilder.setView(dialoglayout);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        mBtnPath = (Button) dialoglayout.findViewById(R.id.btn_path);
        final Button btnDone = (Button) dialoglayout.findViewById(R.id.btn_ok);
        final Button btnCancel = (Button) dialoglayout.findViewById(R.id.btn_cancel);
        mBtnPath.setText(CommonUtils.getFileDirectory(this));
        mBtnPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.show(getFragmentManager(), null);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSharedPreferences.edit().putString(Constants.PATH_PREFERENCE, mBtnPath.getText().toString()).commit();
                mSettingModels.get(1).setPath(mBtnPath.getText().toString());
                mAdapter.notifyItemChanged(1);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onDirectoryPathChangeClick() {
        showDilaogToChangeDownloadPath();
    }

    @Override
    public void onSelectDirectory(@NonNull String path) {
        mBtnPath.setText(path);
        mDialog.dismiss();
    }

    @Override
    public void onCancelChooser() {
        mDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
