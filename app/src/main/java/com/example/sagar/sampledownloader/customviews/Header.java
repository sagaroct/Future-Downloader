package com.example.sagar.sampledownloader.customviews;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.sagar.sampledownloader.R;

/**
 * Created by sagar on 14/7/16.
 */
public class Header extends TextView{
    private Context mContext;

    public Header(Context context) {
        super(context);
        mContext=context;
        init();
    }

    public Header(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        init();
    }

    public Header(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext=context;
        init();
    }

    private void init() {
        setTextSize(getResources().getDimension(R.dimen.default_text_size));
        setTextColor(ContextCompat.getColor(mContext, R.color.colorDownloadTitle));
        setPadding(10,10,10,10);
    }

   public void setTextOfHeader(String text){
       setText(text);
   }
}
