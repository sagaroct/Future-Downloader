package com.example.sagar.sampledownloader.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.sagar.sampledownloader.model.DownloadModel;
import com.example.sagar.sampledownloader.model.DownloadType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagar on 20/7/16.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "downloadsManager.db";

    // Current downloads table name
    private static final String TABLE_CURRENT_DOWNLOADS = "current_downloads";

    // Future downloads table name
    private static final String TABLE_FUTURE_DOWNLOADS = "future_downloads";

    // Past downloads table name
    private static final String TABLE_PAST_DOWNLOADS = "past_downloads";

    // Table Columns names
    private static final String FILE_ID = "file_id";
    private static final String FILE_NAME = "file_name";
    private static final String FILE_URL = "file_url";
    private static final String PROGRESS_DATA = "progress_data";
    private static final String PROGRESS_PERCENTAGE = "progress_percentage";
    private static final String TOTAL_FILE_SIZE = "total_file_size";
    private static final String FUTURE_DOWNLOAD_DATE = "future_download_date";
    private static final String FUTURE_DOWNLOAD_TIME = "future_download_time";
//    private static final String FUTURE_DOWNLOAD_STOP_TIME = "future_download_stop_time";
//    private static final String REPEAT_DOWNLOAD = "repeat_download";

    private static DbHelper mDbHelper;

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public synchronized static DbHelper getInstance(Context context){
        if(mDbHelper == null){
            mDbHelper = new DbHelper(context);
        }
        return mDbHelper;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CURRENT_DOWNLOADS_TABLE = "CREATE TABLE " + TABLE_CURRENT_DOWNLOADS + "("
                + FILE_ID + " LONG PRIMARY KEY," + FILE_NAME + " TEXT,"
                + PROGRESS_DATA + " LONG," + PROGRESS_PERCENTAGE + " INTEGER,"
                + TOTAL_FILE_SIZE + " INTEGER" +")";
       /* String CREATE_FUTURE_DOWNLOADS_TABLE = "CREATE TABLE " + TABLE_FUTURE_DOWNLOADS + "("
                + FILE_ID + " LONG PRIMARY KEY," + FILE_NAME + " TEXT,"+ FILE_URL + " TEXT,"
                + FUTURE_DOWNLOAD_DATE + " TEXT,"
                + FUTURE_DOWNLOAD_TIME + " TEXT,"
                + FUTURE_DOWNLOAD_STOP_TIME + " TEXT "+")";*/
        String CREATE_FUTURE_DOWNLOADS_TABLE = "CREATE TABLE " + TABLE_FUTURE_DOWNLOADS + "("
                + FILE_ID + " LONG PRIMARY KEY," + FILE_NAME + " TEXT,"+ FILE_URL + " TEXT,"
                + FUTURE_DOWNLOAD_DATE + " TEXT,"
                + FUTURE_DOWNLOAD_TIME + " TEXT "+")";
        String CREATE_PAST_DOWNLOADS_TABLE = "CREATE TABLE " + TABLE_PAST_DOWNLOADS + "("
                + FILE_ID + " LONG PRIMARY KEY," + FILE_NAME + " TEXT,"+ FILE_URL + " TEXT,"
                + PROGRESS_DATA + " LONG," + PROGRESS_PERCENTAGE + " INTEGER,"
                + TOTAL_FILE_SIZE + " INTEGER" +")";
        db.execSQL(CREATE_CURRENT_DOWNLOADS_TABLE);
        db.execSQL(CREATE_FUTURE_DOWNLOADS_TABLE);
        db.execSQL(CREATE_PAST_DOWNLOADS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENT_DOWNLOADS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FUTURE_DOWNLOADS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAST_DOWNLOADS);
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new current download
    public void addCurrentDownload(long fileId, String fileName, long progressData, int progressPercentage, int totalSize) {
        Log.d(TAG, "addCurrentDownload: "+ fileId);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FILE_ID, fileId);
        values.put(FILE_NAME, fileName);
        values.put(PROGRESS_DATA, progressData);
        values.put(PROGRESS_PERCENTAGE, progressPercentage);
        values.put(TOTAL_FILE_SIZE, totalSize);
        // Inserting Row
        db.insert(TABLE_CURRENT_DOWNLOADS, null, values);
        db.close(); // Closing database connection
    }

    // Adding new current download
    public void addCurrentDownload(DownloadModel downloadModel) {
        Log.d(TAG, "addCurrentDownload: "+ downloadModel.getFileId());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FILE_ID, downloadModel.getFileId());
        values.put(FILE_NAME, downloadModel.getFileName());
        values.put(PROGRESS_DATA, downloadModel.getDownloadDataProgress());
        values.put(PROGRESS_PERCENTAGE, downloadModel.getDownloadPercentage());
        values.put(TOTAL_FILE_SIZE, downloadModel.getFileSize());
        // Inserting Row
        db.insert(TABLE_CURRENT_DOWNLOADS, null, values);
        db.close(); // Closing database connection
    }

    // Adding new future download
  /*  public void addFutureDownload(long fileId, String fileName, String fileUrl, String date, String time, String stopTime, boolean repeatDownload) {
        Log.d(TAG, "addFutureDownload: "+ fileId);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FILE_ID, fileId);
        values.put(FILE_NAME, fileName);
        values.put(FILE_URL, fileUrl);
        values.put(FUTURE_DOWNLOAD_DATE, date);
        values.put(FUTURE_DOWNLOAD_TIME, time);
        values.put(FUTURE_DOWNLOAD_STOP_TIME, stopTime);
        values.put(REPEAT_DOWNLOAD, repeatDownload);
        // Inserting Row
        db.insert(TABLE_FUTURE_DOWNLOADS, null, values);
        db.close(); // Closing database connection
    }*/

    public void addFutureDownload(long fileId, String fileName, String fileUrl, String date, String time) {
        Log.d(TAG, "addFutureDownload: "+ fileId);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FILE_ID, fileId);
        values.put(FILE_NAME, fileName);
        values.put(FILE_URL, fileUrl);
        values.put(FUTURE_DOWNLOAD_DATE, date);
        values.put(FUTURE_DOWNLOAD_TIME, time);

        // Inserting Row
        db.insert(TABLE_FUTURE_DOWNLOADS, null, values);
        db.close(); // Closing database connection
    }

    // Adding new past download
    public void addPastDownload(String fileId, String fileName, long progressData, int progressPercentage, int totalSize) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FILE_ID, fileId);
        values.put(FILE_NAME, fileName);
        values.put(PROGRESS_DATA, progressData);
        values.put(PROGRESS_PERCENTAGE, progressPercentage);
        values.put(TOTAL_FILE_SIZE, totalSize);
        // Inserting Row
        db.insert(TABLE_PAST_DOWNLOADS, null, values);
        db.close(); // Closing database connection
    }

    // Adding new past download
    public void addPastDownload(DownloadModel downloadModel) {
        Log.d(TAG, "addPastDownload: "+ downloadModel.getFileId());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FILE_ID, downloadModel.getFileId());
        values.put(FILE_NAME, downloadModel.getFileName());
        values.put(FILE_URL, downloadModel.getFileUrl());
        values.put(PROGRESS_DATA, downloadModel.getDownloadDataProgress());
        values.put(PROGRESS_PERCENTAGE, downloadModel.getDownloadPercentage());
        values.put(TOTAL_FILE_SIZE, downloadModel.getFileSize());
        // Inserting Row
        db.insert(TABLE_PAST_DOWNLOADS, null, values);
        db.close(); // Closing database connection
    }

    // Getting current download to be added to past download
     public DownloadModel getCurrentDownload(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CURRENT_DOWNLOADS, new String[] {FILE_ID,
                        FILE_NAME, PROGRESS_DATA, PROGRESS_PERCENTAGE, TOTAL_FILE_SIZE}, FILE_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
         DownloadModel downloadModel = new DownloadModel();
         if (cursor != null) {
             downloadModel.setFileId(cursor.getLong(0));
             downloadModel.setFileName(cursor.getString(1));
             downloadModel.setDownloadDataProgress(cursor.getLong(2));
             downloadModel.setDownloadPercentage(cursor.getInt(3));
             downloadModel.setFileSize(cursor.getInt(4));
             downloadModel.setDownloadType(DownloadType.PAST);
             cursor.close();
         }
        return downloadModel;
    }

    // Getting future download to be added to current download
    public DownloadModel getFutureDownload(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FUTURE_DOWNLOADS, new String[] {FILE_ID,
                        FILE_NAME, FILE_URL, FUTURE_DOWNLOAD_DATE, FUTURE_DOWNLOAD_TIME }, FILE_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        DownloadModel futureDownload = new DownloadModel(cursor.getLong(0),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), DownloadType.CURRENT);
        cursor.close();
        return futureDownload;
    }

    // Getting All CurrentDownloads
    public List<DownloadModel> getAllCurrentDownloads() {
        List<DownloadModel> currentDownloadList = new ArrayList<DownloadModel>();
        String selectQuery = "SELECT  * FROM " + TABLE_CURRENT_DOWNLOADS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DownloadModel currentDownload = new DownloadModel();
                currentDownload.setFileId(cursor.getLong(0));
                currentDownload.setFileName(cursor.getString(1));
                currentDownload.setDownloadDataProgress(cursor.getLong(2));
                currentDownload.setDownloadPercentage(cursor.getInt(3));
                currentDownload.setFileSize(cursor.getInt(4));
                currentDownload.setDownloadType(DownloadType.CURRENT);
                currentDownloadList.add(currentDownload);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return currentDownloadList;
    }

    // Getting All CurrentDownloads
    public List<DownloadModel> getAllFutureDownloads() {
        List<DownloadModel> futureDownloadList = new ArrayList<DownloadModel>();
        String selectQuery = "SELECT  * FROM " + TABLE_FUTURE_DOWNLOADS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DownloadModel futureDownload = new DownloadModel();
                futureDownload.setFileId(cursor.getLong(0));
                futureDownload.setFileName(cursor.getString(1));
                futureDownload.setFileUrl(cursor.getString(2));
                futureDownload.setStrDate(cursor.getString(3));
                futureDownload.setStrTime(cursor.getString(4));
                futureDownload.setDownloadType(DownloadType.FUTURE);
                futureDownloadList.add(futureDownload);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return futureDownloadList;
    }

    // Getting All CurrentDownloads
    public List<DownloadModel> getAllPastDownloads() {
        List<DownloadModel> pastDownloadList = new ArrayList<DownloadModel>();
        String selectQuery = "SELECT  * FROM " + TABLE_PAST_DOWNLOADS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DownloadModel currentDownload = new DownloadModel();
                currentDownload.setFileId(cursor.getLong(0));
                currentDownload.setFileName(cursor.getString(1));
                currentDownload.setFileUrl(cursor.getString(2));
                currentDownload.setDownloadDataProgress(cursor.getLong(3));
                currentDownload.setDownloadPercentage(cursor.getInt(4));
                currentDownload.setFileSize(cursor.getInt(5));
                currentDownload.setDownloadType(DownloadType.PAST);
                pastDownloadList.add(currentDownload);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return pastDownloadList;
    }

    // Updating single current download
    public int updateCurrentDownloadProgress(long fileId, long progressData, int progressPercentage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PROGRESS_DATA, progressData);
        values.put(PROGRESS_PERCENTAGE, progressPercentage);
        // updating row
        return db.update(TABLE_CURRENT_DOWNLOADS, values, FILE_ID + " = ?",
                new String[] {String.valueOf(fileId)});
    }

    // Updating single future download
    public int updateFutureDownload(long fileId, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FUTURE_DOWNLOAD_DATE, date);
        values.put(FUTURE_DOWNLOAD_TIME, time);
        // updating row
        return db.update(TABLE_FUTURE_DOWNLOADS, values, FILE_ID + " = ?",
                new String[] {String.valueOf(fileId)});
    }

    // Deleting single current download
    public void deleteCurrentDownload(long fileId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CURRENT_DOWNLOADS, FILE_ID + " = ?",
                new String[] {String.valueOf(fileId)});
        //commented because it was crashing
//        db.close();
    }

    // Deleting single future download
    public void deleteFutureDownload(long fileId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FUTURE_DOWNLOADS, FILE_ID + " = ?",
                new String[] {String.valueOf(fileId)});
        db.close();
    }

    // Deleting single past download
    public void deletePastDownload(long fileId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PAST_DOWNLOADS, FILE_ID + " = ?",
                new String[] {String.valueOf(fileId)});
        db.close();
    }

}
