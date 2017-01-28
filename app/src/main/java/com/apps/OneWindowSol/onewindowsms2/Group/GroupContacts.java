package com.apps.OneWindowSol.onewindowsms2.Group;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.apps.OneWindowSol.onewindowsms2.R;

import java.util.ArrayList;

public class GroupContacts extends AppCompatActivity {

    private RecyclerView recycleContact;
    private RecyclerView.Adapter adapterContact;
    LinearLayoutManager mLayoutManager;

    ArrayList<Item> contact_details;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_contacts);

        contact_details=new ArrayList<>();

        Intent intent=getIntent();
        recycleContact=(RecyclerView)findViewById(R.id.contactRecycle);
        //adapterContact=new AdapterContact(groupList,groups,getApplication());
        mLayoutManager = new LinearLayoutManager(this);

        recycleContact.setAdapter(adapterContact);
        recycleContact.setLayoutManager(mLayoutManager);
    }
}
