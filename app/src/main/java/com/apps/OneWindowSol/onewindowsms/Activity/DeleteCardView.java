package com.apps.OneWindowSol.onewindowsms.Activity;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.apps.OneWindowSol.onewindowsms.DbHelper;
import com.github.waqarbscs.onewindowsms.R;
import com.apps.OneWindowSol.onewindowsms.SmsModel;

import java.util.ArrayList;
import java.util.List;

public class DeleteCardView extends AppCompatActivity {
    List<SmsModel> data;
    List<String> time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_card_view);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data=new ArrayList<>();
        time=new ArrayList<>();


        Cursor cursor=DbHelper.getDbHelper(this).getHistory();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            SmsModel s=new SmsModel();
            s.setInitialValues();
            s.setTimestampCreated(Long.parseLong(cursor.getString(0)));
            //String z=cursor.getColumnName(0);
            s.setTimestampScheduled(Long.parseLong(cursor.getString(1)));
            s.setRecipientNumber(cursor.getString(2));
            s.setRecipientName(cursor.getString(3));
            s.setStatus(cursor.getString(5));
            s.setMessage(cursor.getString(4));
            //s.setResult(cursor.getString(7));
            cursor.moveToNext();
            if(time.size()>0) {
                if (time.contains(s.getTimestampCreated().toString())) {
                    time.add(s.getTimestampCreated().toString());
                }
                else{
                    time.clear();
                }
            }
            if(time.size()==0) {
                data.add(s);
                time.add(s.getTimestampCreated().toString());
            }
           // data.add(s);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        Recycler_View_Adapter adapter = new Recycler_View_Adapter(data, getApplication());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
