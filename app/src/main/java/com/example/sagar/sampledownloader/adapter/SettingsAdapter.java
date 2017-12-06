package com.example.sagar.sampledownloader.adapter;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sagar.sampledownloader.R;
import com.example.sagar.sampledownloader.common.Constants;
import com.example.sagar.sampledownloader.model.SettingsModel;

import java.util.List;

/**
 * Created by sagar on 1/8/16.
 */
public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int WIFI_PREFERENCE = 0, DIRECTORY_PREFERENCE = 1;
    private List<SettingsModel> items;
    private Context mContext;
    private OnDirectoryPathChangeClickListener mOnDirectoryPathChangeClickListener;

    public interface OnDirectoryPathChangeClickListener {
        void onDirectoryPathChangeClick();
    }

    public void setOnDirectoryPathChangeClickListener(OnDirectoryPathChangeClickListener mOnDirectoryPathChangeClickListener) {
        this.mOnDirectoryPathChangeClickListener = mOnDirectoryPathChangeClickListener;
    }

    public SettingsAdapter(Context context, List<SettingsModel> items) {
        this.mContext = context;
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view;
        switch (viewType) {
            case WIFI_PREFERENCE:
                view = inflater.inflate(R.layout.view_wifi_preference, viewGroup, false);
                viewHolder = new WifiViewHolder(view);
                break;
            case DIRECTORY_PREFERENCE:
                view = inflater.inflate(R.layout.view_directory_preference, viewGroup, false);
                viewHolder = new DirectoryViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        SettingsModel settingsModel = items.get(position);
        switch (viewHolder.getItemViewType()) {
            case WIFI_PREFERENCE:
                WifiViewHolder wifiViewHolder = (WifiViewHolder) viewHolder;
                wifiViewHolder.setWifiSwitch(settingsModel.isWifiChecked());
                wifiViewHolder.setTitle(settingsModel.getTitle());
                break;
            case DIRECTORY_PREFERENCE:
                DirectoryViewHolder directoryViewHolder = (DirectoryViewHolder) viewHolder;
                directoryViewHolder.setPath(settingsModel.getPath());
                directoryViewHolder.setTitle(settingsModel.getTitle());
                break;
        }
    }

    public class WifiViewHolder extends RecyclerView.ViewHolder {

        private SwitchCompat wifi_switch;
        private TextView tvTitle;

        public WifiViewHolder(View v) {
            super(v);
            wifi_switch = (SwitchCompat) v.findViewById(R.id.switch_wifi);
            tvTitle = (TextView) v.findViewById(R.id.tv_title);
            wifi_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean(Constants.WIFI_PREFERENCE, b).commit();
                }
            });
        }

        public void setTitle(String title){
            tvTitle.setText(title);
        }

        public boolean isWifSwitchChecked() {
            return wifi_switch.isChecked();
        }

        public void setWifiSwitch(boolean status) {
            this.wifi_switch.setChecked(status);
        }
    }


    public class DirectoryViewHolder extends RecyclerView.ViewHolder {

        private TextView tvPath;
        private LinearLayout llContainer;
        private TextView tvTitle;

        public DirectoryViewHolder(View v) {
            super(v);
            tvPath = (TextView) v.findViewById(R.id.tv_path);
            tvTitle = (TextView) v.findViewById(R.id.tv_title);
            llContainer = (LinearLayout) v.findViewById(R.id.ll_container);
            llContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnDirectoryPathChangeClickListener.onDirectoryPathChangeClick();
                }
            });
        }

        public void setTitle(String title){
            tvTitle.setText(title);
        }

        public void setPath(String path) {
            tvPath.setText(path);
        }
    }
}