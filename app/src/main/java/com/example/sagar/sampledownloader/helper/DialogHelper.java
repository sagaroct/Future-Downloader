package com.example.sagar.sampledownloader.helper;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.sagar.sampledownloader.R;
import com.example.sagar.sampledownloader.common.CommonUtils;
import com.example.sagar.sampledownloader.common.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sagar on 10/8/16.
 */
public class DialogHelper {

    private AlertDialog.Builder alertDialogBuilder;

    public DialogHelper(Context context, View layoutView) {
        this.alertDialogBuilder = new AlertDialog.Builder(context);
        this.alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setView(layoutView);
    }

    public static void showDialogToDeleteOrRemoveFile(Context context, DialogInterface.OnClickListener onClickListener) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(true);
        CharSequence[] cs = new String[]{context.getString(R.string.remove), context.getString(R.string.remove_delete)};
        alertDialogBuilder.setItems(cs, onClickListener);
        alertDialogBuilder.show();
    }

    public static void showDialogToEditRemove(Context context, DialogInterface.OnClickListener onClickListener) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(true);
        CharSequence[] cs = new String[]{context.getString(R.string.edit_date_time), context.getString(R.string.remove)};
        alertDialogBuilder.setItems(cs, onClickListener);
        alertDialogBuilder.show();
    }

    public static void showDialogToDownloadOverMobileInternet(Context context, DialogInterface.OnClickListener positiveOnClickListener, DialogInterface.OnClickListener negativeOnClickListener) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setTitle(context.getString(R.string.download_using_mobile));
        alertDialogBuilder.setPositiveButton(context.getString(R.string.ok), positiveOnClickListener);
        alertDialogBuilder.setNegativeButton(context.getString(R.string.cancel), negativeOnClickListener);
        alertDialogBuilder.show();
    }

    public static void showDialogToDeleteRemoveCancelFileDownload(Context context, DialogInterface.OnClickListener onClickListener, boolean isPaused) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(true);
        String strResumePause;
        if(isPaused){
            strResumePause = context.getString(R.string.resume_download);
        }
        else{
             strResumePause = context.getString(R.string.pause_download);
        }
        //TODO: Commented as Pause/Resume feature is not working properly
//        CharSequence[] cs = new String[]{strResumePause, context.getString(R.string.remove), context.getString(R.string.remove_delete)};
        CharSequence[] cs = new String[]{context.getString(R.string.remove), context.getString(R.string.remove_delete)};
        alertDialogBuilder.setItems(cs, onClickListener);
        alertDialogBuilder.show();
    }

    public static void showDatePickerDialog(final Context context, final Button btnDate, final Button btnTime, final Button btnOk) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
                        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                            if (!CommonUtils.isDateAfterCurrentDay(date)) {
                                Toast.makeText(context, R.string.warning_past_date_selected, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
                        String filterDate = formatter.format(date);
                        btnDate.setText(filterDate);
                        if (btnOk!=null && TextUtils.isEmpty(btnTime.getText())){
                            btnOk.setEnabled(false);
                        }
                        if(btnTime != null){
                            btnTime.setEnabled(true);
                        }
                    }
                }, mYear, mMonth, mDay);
        dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dpd.setTitle(context.getString(R.string.select_date));
        dpd.show();
    }

    public static void showTimePickerDialog(Context context, final Button btnTime, final Button btnOk) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                btnTime.setText(String.format(Locale.getDefault(), "%s:%s", CommonUtils.pad(selectedHour), CommonUtils.pad(selectedMinute)));
                if(!btnOk.isEnabled()){
                    btnOk.setEnabled(true);
                }
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle(context.getString(R.string.select_time));
        mTimePicker.show();
    }

    public static void showStopTimePickerDialog(Context context, final Button btnTime, final CheckBox repeatCheckBox) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                btnTime.setText(String.format(Locale.getDefault(), "%s:%s", CommonUtils.pad(selectedHour), CommonUtils.pad(selectedMinute)));
                if(!repeatCheckBox.isEnabled()){
                    repeatCheckBox.setEnabled(true);
                }
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle(context.getString(R.string.select_time));
        mTimePicker.show();
    }

    public AlertDialog create(){
        return alertDialogBuilder.create();
    }

    public AlertDialog.Builder getAlertDialogBuilder() {
        return alertDialogBuilder;
    }

    public static void showDialogToDisplayText(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
