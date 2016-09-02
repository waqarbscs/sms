package com.apps.OneWindowSol.onewindowsms.Activity;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.apps.OneWindowSol.onewindowsms.DbHelper;
import com.github.waqarbscs.onewindowsms.R;
import com.apps.OneWindowSol.onewindowsms.SmsModel;

public class Delete extends ListActivity {
    private final static int REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.item_add);
        LayoutInflater lf = getLayoutInflater();
        View headerView = lf.inflate(R.layout.activity_delete, getListView(), false);
        headerView.setClickable(true);
        getListView().addHeaderView(headerView);
        getListView().setBackgroundResource(R.drawable.sms_screen);
        /*
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), AddSmsActivity.class);
                intent.putExtra(DbHelper.COLUMN_TIMESTAMP_CREATED, String.valueOf(id));
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        */
    }
    @Override
    protected void onResume() {
        super.onResume();
        setListAdapter(getSmsListAdapter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
        adapter.getCursor().close();
        DbHelper.closeDbHelper();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
        adapter.getCursor().close();
        DbHelper.closeDbHelper();
    }

    private SimpleCursorAdapter getSmsListAdapter() {


        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                DbHelper.getDbHelper(this).getCursor1(),
                new String[] { DbHelper.COLUMN_MESSAGE, DbHelper.COLUMN_RECIPIENT_NAME },
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                TextView textView = (TextView) view;
                if (textView.getId() == android.R.id.text2) {
                    textView.setText(getFormattedSmsInfo(cursor));
                    return true;
                }
                return false;
            }
        });
        return adapter;
    }
    private String getFormattedSmsInfo(Cursor cursor) {
        String recipient, recipientName, recipientNumber, status="", datetime;
        recipientName = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_RECIPIENT_NAME));
        recipientNumber = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_RECIPIENT_NUMBER));
        recipient = recipientName.length() > 0 ? recipientName : recipientNumber;
        switch (cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_STATUS))) {
            case (SmsModel.STATUS_PENDING):
                status = getString(R.string.list_status_pending);
                break;
            case (SmsModel.STATUS_SENT):
                status = getString(R.string.list_status_sent);
                break;
            case (SmsModel.STATUS_DELIVERED):
                status = getString(R.string.list_status_delivered);
                break;
            default:
                status = getString(R.string.list_status_failed);
        }
        datetime = DateUtils.formatDateTime(
                this,
                cursor.getLong(cursor.getColumnIndex(DbHelper.COLUMN_TIMESTAMP_SCHEDULED)),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME
        );
        return getString(R.string.list_sms_info_template, status, recipient, datetime);
    }
}