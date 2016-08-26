package com.github.waqarbscs.onewindowsms.Activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.waqarbscs.onewindowsms.AlarmReceiver;
import com.github.waqarbscs.onewindowsms.DbHelper;
import com.github.waqarbscs.onewindowsms.R;
import com.github.waqarbscs.onewindowsms.SmsModel;

import java.util.Collections;
import java.util.List;

/**
 * Created by waqarbscs on 8/26/2016.
 */
public class Recycler_View_Adapter1 extends  RecyclerView.Adapter<Recycler_View_Adapter1.View_Holder> {
    List<SmsModel> list = Collections.emptyList();
    Context context;
    private final static int REQUEST_CODE = 1;

    public Recycler_View_Adapter1(List<SmsModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        context=parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_detail, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;

    }
    private void unscheduleAlarm(SmsModel sms) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(getAlarmPendingIntent(sms));
    }

    private PendingIntent getAlarmPendingIntent(SmsModel sms) {
        Intent intent = new Intent(AlarmReceiver.INTENT_FILTER);
        intent.putExtra(DbHelper.COLUMN_TIMESTAMP_CREATED, sms.getTimestampCreated());
        return PendingIntent.getBroadcast(
                context,
                sms.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT & Intent.FILL_IN_DATA
        );
    }
    @Override
    public void onBindViewHolder(View_Holder holder, int position) {
        String datetime = DateUtils.formatDateTime(
                context,
                list.get(position).getTimestampScheduled(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME
        );
        final int p=position;
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.title.setText(list.get(position).getMessage());
        holder.description.setText(context.getResources().getString(R.string.list_sms_info_template, list.get(position).getStatus(), list.get(position).getRecipientNumber(), datetime));
         holder.deleteButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 DbHelper.getDbHelper(context).delete(list.get(p).getTimestampCreated());
                 unscheduleAlarm(list.get(p));
                 remove(list.get(p));
             }
         });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddSmsActivity.class);
                intent.putExtra(DbHelper.COLUMN_TIMESTAMP_CREATED, list.get(p).getTimestampCreated().toString());
                ((Activity)context).startActivityForResult(intent, REQUEST_CODE);
            }
        });

        //animate(holder);

    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, SmsModel data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(SmsModel data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }

    public class View_Holder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView title;
        TextView description;
        ImageView imageView,deleteButton;

        View_Holder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            imageView = (ImageView) itemView.findViewById(R.id.editButton);
            deleteButton = (ImageView) itemView.findViewById(R.id.deletButton);
        }
    }
}
