package com.example.sagar.sampledownloader.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.example.sagar.sampledownloader.R;

/**
 * Created by sagar on 14/7/16.
 */
public class TextProgressBar extends ProgressBar {
    private String mText;
    private String mProgressText;
    private TextPaint textPaint;

    public TextProgressBar(Context context) {
        super(context);
        textPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        textPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        textPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect bounds = new Rect();
        textPaint.getTextBounds(mText, 0, mText.length(), bounds);
        int x = getWidth()  - 20;
        int y = getHeight() / 2 - bounds.centerY();
        //For file name
        textPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.default_small_size));
//        setTextSizeForWidth(textPaint, getWidth()/2 + 20, mText);
        textPaint.setTextAlign(Paint.Align.LEFT);
        CharSequence txt = TextUtils.ellipsize(mText, textPaint, getWidth()/2 + 20, TextUtils.TruncateAt.END);
        canvas.drawText(txt.toString(), 10, y, textPaint);
        //For download progress percentage
        textPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(mProgressText, x, y, textPaint);
    }

    /**
     * Sets the text size for a Paint object so a given string of text will be a
     * given width.
     *
     * @param paint
     *            the Paint to set the text size for
     * @param desiredWidth
     *            the desired width
     * @param text
     *            the text that should be that width
     */
    private void setTextSizeForWidth(Paint paint, float desiredWidth,
                                            String text) {
//        final float testTextSize = 48f;
        int testTextSize = getResources().getDimensionPixelSize(R.dimen.default_small_size);
        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = testTextSize * desiredWidth / bounds.width();
        // Set the paint for that size.
//        paint.setTextSize(desiredTextSize);
    }

    public synchronized void setText(String mText) {
        this.mText = mText;
        drawableStateChanged();
    }

    public synchronized void setProgressPercentage(int progress) {
        this.mProgressText = progress+"%";
        drawableStateChanged();
    }

    public void setTextColor(int color) {
        textPaint.setColor(color);
        drawableStateChanged();
    }

    public void setTextSize(float size) {
        textPaint.setTextSize(size);
        drawableStateChanged();
    }
}

