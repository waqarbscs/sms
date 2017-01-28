package com.apps.OneWindowSol.onewindowsms2.Group;

import android.app.Application;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apps.OneWindowSol.onewindowsms2.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by waqarbscs on 11/17/2016.
 */
public class AdapterContact extends RecyclerView.Adapter<AdapterContact.ViewHolder>  {
    Context context;
    LinkedHashMap<Item, ArrayList<Item>> groupList;
    List<Item> groups;
    public AdapterContact(LinkedHashMap<Item, ArrayList<Item>> groupList, List<Item> groups, Context context) {
        this.context=context;
        this.groupList=groupList;
        this.groups=groups;
    }

    @Override
    public AdapterContact.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_group_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters

        AdapterContact.ViewHolder vh = new AdapterContact.ViewHolder(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(AdapterContact.ViewHolder holder, int position) {

        holder.groupName.setText(groups.get(position).name);
    }



    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView groupName;
        public ViewHolder(View itemView) {
            super(itemView);
            groupName=(TextView)itemView.findViewById(R.id.groupName);
        }
    }
}
