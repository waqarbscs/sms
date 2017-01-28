package com.apps.OneWindowSol.onewindowsms2.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.apps.OneWindowSol.onewindowsms2.Group.AdapterGroup;
import com.apps.OneWindowSol.onewindowsms2.Group.Item;
import com.apps.OneWindowSol.onewindowsms2.R;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.LinkedHashMap;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    private static final String GROUP_TYPE="com.onewindowsol.contactsgroup";
    private FloatingActionButton btnCreateGroup;

    private RecyclerView recycleGroup;
    private RecyclerView.Adapter adapterGroup;
    LinearLayoutManager mLayoutManager;

    private LinkedHashMap<Item,ArrayList<Item>> groupList;
    List<Item> groups;
    List<ArrayList<Item>> groupContacts;
    int GroupId=0;

    ProgressDialog progressDialog;

    private final static int REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        //editCreateGroup=(EditText)findViewById(R.id.editCreateGroup);
        btnCreateGroup=(FloatingActionButton) findViewById(R.id.floatingActionButton);

        //groupList=new ArrayList<>();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Adding contacts in group");
        progressDialog.setCancelable(false);


        groupList = new LinkedHashMap<Item,ArrayList<Item>>();

        groupList=initContactList();
        groups=new ArrayList<>(groupList.keySet());
        groupContacts=new ArrayList<>(groupList.values());

        recycleGroup=(RecyclerView)findViewById(R.id.recycleGroup);
        adapterGroup=new AdapterGroup(groupList,groups,getApplication());
        mLayoutManager = new LinearLayoutManager(this);

        recycleGroup.setAdapter(adapterGroup);
        recycleGroup.setLayoutManager(mLayoutManager);


        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //android.permission.WRITE_CONTACTS
                //createGroup();
                //android.permission.READ_CONTACTS
                /*
                for(int i=0;i<fetchGroups().size();i++) {
                    groupList.add(fetchGroups().get(i).name);
                }
                */
                showDialog1();
                //Toast.makeText(MainActivity.this, ""+groupList.get(0).toString(), Toast.LENGTH_SHORT).show();
            }
        });

        recycleGroup.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplication(),recycleGroup, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                        Intent i=new Intent(GroupActivity.this,ContactBook.class);
                        i.putExtra("Activ","ab");
                        startActivityForResult(i,REQUEST_CODE);
                        GroupId= Integer.parseInt(groups.get(position).id);

                        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                        //Intent i=new Intent(GroupActivity.this,GroupContacts.class);
                        //i.putStringArrayListExtra("contactDetails",groupContacts.get(position));
                        //startActivity(i);
                    }
                })
        );


    }
    public void showDialog1(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GroupActivity.this);
        alertDialog.setTitle("Create Group");


        final EditText input = new EditText(GroupActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        //alertDialog.setIcon(R.drawable.key);

        alertDialog.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //insertType(type,input.getText().toString());
                        if(input.getText().length()>0) {
                            createGroup(input.getText().toString());
                        }else {
                            Toast.makeText(GroupActivity.this, "Please submit a name", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        alertDialog.setNegativeButton("Discard",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode != 0) {

            switch (resultCode) {
                case ContactBook.RESULT_SCHEDULED:
                    progressDialog.show();
                    Thread backgroundThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences contactP = getSharedPreferences("contactP", 0);
                            int size1 = contactP.getInt("size", 0);
                            for (int j = 0; j < size1; j++) {
                                String s1 = contactP.getString("val" + j, "");
                                long s2 = getRawContactId(Integer.parseInt(s1));
                                //deleteContactFromGroup(s2,GroupId);
                                addToGroup(s2, GroupId);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    //RestartActivity();
                                    recreate();

                                }
                            });
                        }
                     });
                    backgroundThread.start();


                    break;
            }
        }
    }
    @Override
    public void recreate()
    {
        super.recreate();

    }
    private void deleteContactFromGroup(long contactId, long groupId)
    {
        ContentResolver cr = getContentResolver();
        String where = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + "=" + groupId + " AND "
                + ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID + "=?" + " AND "
                + ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE + "='"
                + ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE + "'";


            try
            {
                cr.delete(ContactsContract.Data.CONTENT_URI, where,
                        new String[] { String.valueOf(contactId) });
            } catch (Exception e)
            {
                e.printStackTrace();
            }

    }
    public int getRawContactId(int contactId) {
        String[] projection = new String[]{ContactsContract.RawContacts._ID};
        String selection = ContactsContract.RawContacts.CONTACT_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(contactId)};
        Cursor c = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
        c.moveToNext();
        int rawContactId = c.getInt(c.getColumnIndex(ContactsContract.RawContacts._ID));
        c.close();
        return rawContactId;
    }

    public void addToGroup(long personId, long groupId) {

        //remove if exists
        //this.removeFromGroup(personId, groupId);
        deleteContactFromGroup(personId,GroupId);

        ContentValues values = new ContentValues();
        values.put(ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID,
                personId);
        values.put(
                ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,
                groupId);
        values.put(
                ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE,
                ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE);

        getContentResolver().insert(
                ContactsContract.Data.CONTENT_URI, values);

    }



    private void createGroup(String Name) {
        boolean exist=false;
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Groups.CONTENT_URI)
                .withValue(
                        ContactsContract.Groups.TITLE,
                        Name)
                .withValue(
                        ContactsContract.Groups.ACCOUNT_TYPE,
                        GROUP_TYPE)
                .withValue(
                        ContactsContract.Groups.ACCOUNT_NAME,
                       "Phone")
                .build());
        try {
            for(Item item : groups){
                if(item.name.toLowerCase().equals(Name.toLowerCase())){
                    exist=true;
                }
            }
            if(!exist) {
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                RestartActivity();
            }
            else {
                Toast.makeText(this, "Group having title "+Name+" already exist", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }
    public void RestartActivity(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
    private ArrayList<Item> fetchGroups(){
        ArrayList<Item> groupList = new ArrayList<Item>();
        String[] projection = new String[]{ContactsContract.Groups._ID,ContactsContract.Groups.TITLE};
        Cursor cursor = getContentResolver().query(ContactsContract.Groups.CONTENT_URI,
                projection, null, null, null);
        ArrayList<String> groupTitle = new ArrayList<String>();
        while(cursor.moveToNext()){
            Item item = new Item();
            item.id = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups._ID));
            String groupName =      cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE));

            if(groupName.contains("Group:"))
                groupName = groupName.substring(groupName.indexOf("Group:")+"Group:".length()).trim();

            if(groupName.contains("Favorite_"))
                groupName = "Favorite";

            if(groupName.contains("Starred in Android") || groupName.contains("My Contacts"))
                continue;
            if(groupTitle.contains(groupName)){
                for(Item group:groupList){
                    if(group.name.equals(groupName)){
                        group.id += ","+item.id;
                        break;
                    }
                }
            }else{
                groupTitle.add(GROUP_TYPE);
                item.name = groupName;
                groupList.add(item);
            }

        }

        cursor.close();
        Collections.sort(groupList,new Comparator<Item>() {
            public int compare(Item item1, Item item2) {
                return item2.name.compareTo(item1.name)<0
                        ?0:-1;
            }
        });

        return groupList;
    }
    private LinkedHashMap<Item, ArrayList<Item>> initContactList(){
        LinkedHashMap<Item,ArrayList<Item>> groupList = new LinkedHashMap<Item,ArrayList<Item>>();
        ArrayList<Item> groupsList = fetchGroups();
        for(Item item:groupsList){
            String[] ids = item.id.split(",");
            ArrayList<Item> groupMembers =new ArrayList<Item>();
            for(int i=0;i<ids.length;i++){
                String groupId = ids[i];
                groupMembers.addAll(fetchGroupMembers(groupId));
            }
            item.name = item.name+" ("+groupMembers.size()+")";
            groupList.put(item,groupMembers);
        }
        return groupList;
    }
    private ArrayList<Item> fetchGroupMembers(String groupId){
        ArrayList<Item> groupMembers = new ArrayList<Item>();
        String where =  ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID +"="+groupId
                +" AND "
                + ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE+"='"
                + ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE+"'";
        String[] projection = new String[]{ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID, ContactsContract.Data.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection, where,null,
                ContactsContract.Data.DISPLAY_NAME+" COLLATE LOCALIZED ASC");
        while(cursor.moveToNext()){
            Item item = new Item();
            item.name = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            item.id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID));
            Cursor phoneFetchCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.TYPE},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+item.id,null,null);
            while(phoneFetchCursor.moveToNext()){
                item.phNo = phoneFetchCursor.getString(phoneFetchCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                item.phDisplayName = phoneFetchCursor.getString(phoneFetchCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                item.phType = phoneFetchCursor.getString(phoneFetchCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            }
            phoneFetchCursor.close();
            groupMembers.add(item);
        }
        cursor.close();
        return groupMembers;
    }

}
