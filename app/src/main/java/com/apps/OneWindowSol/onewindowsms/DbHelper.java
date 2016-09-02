package com.apps.OneWindowSol.onewindowsms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class DbHelper extends SQLiteOpenHelper {

    private static DbHelper dbHelper;

    private static final String DATABASE_NAME = "SmsScheduler.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_SMS = "sms";

    public static final String COLUMN_TIMESTAMP_CREATED = "datetimeCreated";
    public static final String COLUMN_TIMESTAMP_SCHEDULED = "datetimeScheduled";
    public static final String COLUMN_RECIPIENT_NUMBER = "recipientNumber";
    public static final String COLUMN_RECIPIENT_NAME = "recipientName";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_RESULT = "result";
    public static final String COLUMN_RECURSIVE="recursive";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DbHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    static public DbHelper getDbHelper(Context context) {
        if (null == dbHelper) {
            dbHelper = new DbHelper(context);
        }
        return dbHelper;
    }

    static public void closeDbHelper() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SMS_TABLE = "CREATE TABLE " + TABLE_SMS +
                "(" +
                COLUMN_TIMESTAMP_CREATED + " BIGINTEGER," +
                COLUMN_TIMESTAMP_SCHEDULED + " BIGINTEGER," +
                COLUMN_RECIPIENT_NUMBER + " TEXT," +
                COLUMN_RECIPIENT_NAME + " TEXT," +
                COLUMN_MESSAGE + " TEXT," +
                COLUMN_STATUS + " TEXT," +
                COLUMN_RESULT + " TEXT," +
                COLUMN_RECURSIVE + " TEXT" +
                ")";
        db.execSQL(CREATE_SMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS);
            onCreate(db);
        }
    }

    public void save(SmsModel sms,String value) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIMESTAMP_SCHEDULED, sms.getTimestampScheduled());
        values.put(COLUMN_RECIPIENT_NAME, sms.getRecipientName());
        values.put(COLUMN_RECIPIENT_NUMBER, sms.getRecipientNumber());
        values.put(COLUMN_MESSAGE, sms.getMessage());
        values.put(COLUMN_STATUS, sms.getStatus());
        values.put(COLUMN_RESULT, sms.getResult());
        values.put(COLUMN_RECURSIVE,sms.getRecursive());
        if(value.equals("insert")) {
            if (sms.getTimestampCreated() > 0) {
                values.put(COLUMN_TIMESTAMP_CREATED, sms.getTimestampCreated());
                dbHelper.getWritableDatabase().insert(TABLE_SMS, null,values);
            } else {
                long timestampCreated = Calendar.getInstance().getTimeInMillis();
                sms.setTimestampCreated(timestampCreated);
                values.put(COLUMN_TIMESTAMP_CREATED, timestampCreated);
                dbHelper.getWritableDatabase().insert(TABLE_SMS, null, values);
            }
        }else{
            if (sms.getTimestampCreated() > 0) {
                String whereClause = COLUMN_TIMESTAMP_CREATED + "=?";
                String[] whereArgs = new String[]{sms.getTimestampCreated().toString()};
                dbHelper.getWritableDatabase().update(TABLE_SMS, values, whereClause, whereArgs);
            }
        }
    }
    public void save1(SmsModel sms,String value,String num) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIMESTAMP_SCHEDULED, sms.getTimestampScheduled());
        values.put(COLUMN_RECIPIENT_NAME, sms.getRecipientName());
        values.put(COLUMN_RECIPIENT_NUMBER, sms.getRecipientNumber());
        values.put(COLUMN_MESSAGE, sms.getMessage());
        values.put(COLUMN_STATUS, sms.getStatus());
        values.put(COLUMN_RESULT, sms.getResult());
        values.put(COLUMN_RECURSIVE,sms.getRecursive());
        if(value.equals("insert")) {
            if (sms.getTimestampCreated() > 0) {
                values.put(COLUMN_TIMESTAMP_CREATED, sms.getTimestampCreated());
                dbHelper.getWritableDatabase().insert(TABLE_SMS, null,values);
            } else {
                long timestampCreated = Calendar.getInstance().getTimeInMillis();
                sms.setTimestampCreated(timestampCreated);
                values.put(COLUMN_TIMESTAMP_CREATED, timestampCreated);
                dbHelper.getWritableDatabase().insert(TABLE_SMS, null, values);
            }
        }else{
            if (sms.getTimestampCreated() > 0) {
                String whereClause = COLUMN_TIMESTAMP_CREATED + "=?"+" AND "+COLUMN_RECIPIENT_NUMBER + "=?";
                String[] whereArgs = new String[]{sms.getTimestampCreated().toString(),num};
                dbHelper.getWritableDatabase().update(TABLE_SMS, values, whereClause, whereArgs);
            }
        }
    }

    public Cursor getCursor() {
        String selection = "";
        return getCursor(selection);
    }

    public Cursor getname(String t){
        String selection=COLUMN_TIMESTAMP_CREATED+"=?";
        String[]  selectionArgs = new String[] {t};
        return dbHelper.getReadableDatabase().query(TABLE_SMS,new String[]{"recipientName","recipientNumber","recursive"},selection,selectionArgs,null,null,null);
        //return  dbHelper.getReadableDatabase().rawQuery("select * from "+TABLE_SMS,null);
    }

    public Cursor getHistory(){
        String[] columns = new String[] { "*", COLUMN_TIMESTAMP_CREATED + " AS _id" };
        String[]  selectionArgs;
        String selection = "";
        selection = COLUMN_STATUS + "!=?";
        selectionArgs = new String[] {SmsModel.STATUS_PENDING};
        String orderBy = COLUMN_TIMESTAMP_CREATED + " DESC";
        return dbHelper.getReadableDatabase().query(TABLE_SMS, columns, selection, selectionArgs, null, null, orderBy);
        //return  dbHelper.getReadableDatabase().rawQuery("select * from "+TABLE_SMS,null);
    }
    public Cursor getDetail(){
        String[] columns = new String[] { "*", COLUMN_TIMESTAMP_CREATED + " AS _id" };
        String[]  selectionArgs;
        String selection = "";
        selection = COLUMN_STATUS + "=?";
        selectionArgs = new String[] {SmsModel.STATUS_PENDING};
        String orderBy = COLUMN_TIMESTAMP_CREATED + " DESC";
        return dbHelper.getReadableDatabase().query(TABLE_SMS, columns, selection, selectionArgs, null, null, orderBy);
        //return  dbHelper.getReadableDatabase().rawQuery("select * from "+TABLE_SMS,null);
    }
    public Cursor getCursor(String status) {
        String[] columns = new String[] { "*", COLUMN_TIMESTAMP_CREATED + " AS _id" };
        String selection = "";
        String[] selectionArgs = new String[]{};
        if (null != status && status.length() > 0) {
            selection = COLUMN_STATUS + "=?";
            selectionArgs = new String[] {status};
        }else{
            selection = COLUMN_STATUS + "=?";
            selectionArgs = new String[] {SmsModel.STATUS_PENDING};
        }
        String orderBy = COLUMN_TIMESTAMP_CREATED + " DESC";
        return dbHelper.getReadableDatabase().query(TABLE_SMS, columns, selection, selectionArgs, null, null, orderBy);
    }
    public Cursor getCursor1() {
        String selection = "";
        return getCursor1(selection);
    }
    public Cursor getCursor1(String status) {
        String[] columns = new String[] { "*", COLUMN_TIMESTAMP_CREATED + " AS _id" };
        String selection = "";
        String[] selectionArgs = new String[]{};
        if (null != status && status.length() > 0) {
            selection = COLUMN_STATUS + "=?";
            selectionArgs = new String[] {status};
        }else{
            selection = COLUMN_STATUS + "=?";
            selectionArgs = new String[] {SmsModel.STATUS_FAILED};
        }
        String orderBy = COLUMN_TIMESTAMP_CREATED + " DESC";
        return dbHelper.getReadableDatabase().query(TABLE_SMS, columns, selection, selectionArgs, null, null, orderBy);
    }

    public SmsModel get(long timestampCreated) {
        Cursor cursor = dbHelper.getReadableDatabase().query(
                false,
                TABLE_SMS,
                new String[]{"*", COLUMN_TIMESTAMP_CREATED + " AS _id"},
                COLUMN_TIMESTAMP_CREATED + "=?",
                new String[]{Long.toString(timestampCreated)},
                null,
                null,
                null,
                "1"
        );
        int f=cursor.getCount();
        if (cursor != null) {
                ArrayList<SmsModel> results = getObjects(cursor);
                cursor.close();
                if (results.size() > 0) {
                    return results.get(0);
                }

        }
        return null;
    }

    public ArrayList<SmsModel> get(String status) {
        Cursor cursor = getCursor(status);
        if (cursor != null) {
            ArrayList<SmsModel> results = getObjects(cursor);
            cursor.close();
            return results;
        }
        return null;
    }

    public void delete(Long timestampCreated) {
        String selection = COLUMN_TIMESTAMP_CREATED + "=?";
        String[] selectionArgs = new String[] {timestampCreated.toString()};
        dbHelper.getReadableDatabase().delete(TABLE_SMS, selection, selectionArgs);
    }
    public void delete1(Long timestampCreated,String d) {
        String selection = COLUMN_TIMESTAMP_CREATED+"=?"+"AND "+COLUMN_RECIPIENT_NUMBER + "=?";
        String[] selectionArgs = new String[] {timestampCreated.toString(),d};
        dbHelper.getReadableDatabase().delete(TABLE_SMS, selection, selectionArgs);
    }
    private ArrayList<SmsModel> getObjects(Cursor cursor) {
        ArrayList<SmsModel> result = new ArrayList<>();
        int indexTimestampCreated = cursor.getColumnIndex(COLUMN_TIMESTAMP_CREATED);
        int indexTimestampScheduled = cursor.getColumnIndex(COLUMN_TIMESTAMP_SCHEDULED);
        int indexRecipientNumber = cursor.getColumnIndex(COLUMN_RECIPIENT_NUMBER);
        int indexRecipientName = cursor.getColumnIndex(COLUMN_RECIPIENT_NAME);
        int indexMessage = cursor.getColumnIndex(COLUMN_MESSAGE);
        int indexStatus = cursor.getColumnIndex(COLUMN_STATUS);
        int indexResult = cursor.getColumnIndex(COLUMN_RESULT);
        int indexRecursive = cursor.getColumnIndex(COLUMN_RECURSIVE);
        SmsModel object;
        while (cursor.moveToNext()) {
            object = new SmsModel();
            object.setTimestampCreated(cursor.getLong(indexTimestampCreated));
            object.setTimestampScheduled(cursor.getLong(indexTimestampScheduled));
            object.setRecipientNumber(cursor.getString(indexRecipientNumber));
            object.setRecipientName(cursor.getString(indexRecipientName));
            object.setMessage(cursor.getString(indexMessage));
            object.setStatus(cursor.getString(indexStatus));
            object.setResult(cursor.getString(indexResult));
            object.setRecursive(cursor.getString(indexRecursive));
            result.add(object);
        }
        return result;
    }
}