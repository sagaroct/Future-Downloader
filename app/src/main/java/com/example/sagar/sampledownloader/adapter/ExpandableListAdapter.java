package com.example.sagar.sampledownloader.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.sagar.sampledownloader.R;
import com.example.sagar.sampledownloader.common.CommonUtils;
import com.example.sagar.sampledownloader.common.Constants;
import com.example.sagar.sampledownloader.customviews.TextProgressBar;
import com.example.sagar.sampledownloader.model.DownloadModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sagar on 25/7/16.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private static final String TAG = ExpandableListAdapter.class.getSimpleName();

    private List<String> mListDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<DownloadModel>> mListDataChild;

    public ExpandableListAdapter(List<String> listDataHeader,
                                 HashMap<String, List<DownloadModel>> listChildData) {
        this.mListDataHeader = listDataHeader;
        this.mListDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final DownloadModel downloadModel = (DownloadModel) getChild(groupPosition, childPosition);
        int childType = getChildType(groupPosition, childPosition);
        Log.d(TAG, "groupPosition: "+groupPosition + " childPosition: "+childPosition);
        LayoutInflater infalInflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Integer integerTag = null;
        if(convertView!=null){
            integerTag= (Integer) convertView.getTag();
        }
        if (convertView == null || integerTag.intValue() != childType) {
            switch (childType) {
                case Constants.CURRENT_DOWNLOAD: convertView = infalInflater.inflate(R.layout.view_current_downloads, parent, false);
                    convertView.setTag(Integer.valueOf(childType));
                    break;
                case Constants.PAST_DOWNLOAD:
                    convertView = infalInflater.inflate(R.layout.view_past_download, parent, false);
                    convertView.setTag(Integer.valueOf(childType));
                    break;

                case Constants.FUTURE_DOWNLOAD:
                    convertView = infalInflater.inflate(R.layout.view_future_download, parent, false);
                    convertView.setTag(Integer.valueOf(childType));
                    break;
            }
        }

        switch (childType) {
            case Constants.CURRENT_DOWNLOAD:
                TextProgressBar txtProgressBar = (TextProgressBar) convertView.findViewById(R.id.tv_progress_bar);
                TextView txtDownloadPercentage = (TextView) convertView.findViewById(R.id.tv_download_progress);
                txtProgressBar.setText(downloadModel.getFileName());
                txtProgressBar.setProgress(downloadModel.getDownloadPercentage());
                txtProgressBar.setProgressPercentage(downloadModel.getDownloadPercentage());
                txtDownloadPercentage.setText(String.format("%s/\n%s", CommonUtils.getStringByteSize(downloadModel.getDownloadDataProgress()), CommonUtils.getStringByteSize(downloadModel.getFileSize())));
                break;
            case Constants.PAST_DOWNLOAD: {
                TextView txtFileName = (TextView) convertView.findViewById(R.id.tv_file_name);
                txtFileName.setText(downloadModel.getFileName());
                break;
            }

            case Constants.FUTURE_DOWNLOAD:
                TextView txtFileName = (TextView) convertView.findViewById(R.id.tv_file_name);
                TextView txtDateTime = (TextView) convertView.findViewById(R.id.tv_date_time);
                txtFileName.setText(downloadModel.getFileName());
                txtDateTime.setText(String.format("%s\n%s", downloadModel.getStrDate(), downloadModel.getStrTime()));
                break;
        }
        return convertView;
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mListDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
//        String[] headerArray = headerTitle.split(":");
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.view_header, parent, false);
        }
        TextView txtHeader = (TextView) convertView.findViewById(R.id.tv_title);
//        TextView txtSubTitle = (TextView) convertView.findViewById(R.id.tv_sub_title);
        txtHeader.setText(headerTitle);
//        txtSubTitle.setText(headerArray[1]);
        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        switch (groupPosition) {
            case 0:
                return Constants.CURRENT_DOWNLOAD;
            case 1:
                return Constants.FUTURE_DOWNLOAD;
            case 2:
                return Constants.PAST_DOWNLOAD;
            default:
                return Constants.CURRENT_DOWNLOAD;
        }
    }

    @Override
    public int getChildTypeCount() {
        return 3;
    }

    @Override
    public int getGroupTypeCount() {
        return 1;
    }
}
