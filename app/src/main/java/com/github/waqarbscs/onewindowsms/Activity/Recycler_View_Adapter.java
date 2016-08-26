package com.github.waqarbscs.onewindowsms.Activity;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.waqarbscs.onewindowsms.DbHelper;
import com.github.waqarbscs.onewindowsms.R;
import com.github.waqarbscs.onewindowsms.SmsModel;

import java.util.Collections;
import java.util.List;

/**
 * Created by waqarbscs on 8/26/2016.
 */
public class Recycler_View_Adapter extends  RecyclerView.Adapter<Recycler_View_Adapter.View_Holder> {
    List<SmsModel> list = Collections.emptyList();
    Context context;

    public Recycler_View_Adapter(List<SmsModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(View_Holder holder, int position) {
        String datetime = DateUtils.formatDateTime(
                context,
                list.get(position).getTimestampScheduled(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME
        );
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.title.setText(list.get(position).getMessage());
        holder.description.setText(context.getResources().getString(R.string.list_sms_info_template, list.get(position).getStatus(), list.get(position).getRecipientNumber(), datetime));
       // holder.imageView.setImageResource(list.get(position).imageId);

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
        //ImageView imageView,deleteButton;

        View_Holder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            //imageView = (ImageView) itemView.findViewById(R.id.editButton);
            //imageView = (ImageView) itemView.findViewById(R.id.deletButton);
        }
    }
}
