package com.apps.OneWindowSol.onewindowsms2.Activity;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.widget.Button;
import android.widget.EditText;

import com.apps.OneWindowSol.onewindowsms2.DbHelper;
import com.apps.OneWindowSol.onewindowsms2.Group.AdapterGroup;
import com.apps.OneWindowSol.onewindowsms2.Group.Item;
import com.apps.OneWindowSol.onewindowsms2.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GroupActivity extends AppCompatActivity {

    private static final String GROUP_TYPE="com.onewindowsol.contactsgroup";
    private EditText editCreateGroup;
    private FloatingActionButton btnCreateGroup;

    private RecyclerView recycleGroup;
    private RecyclerView.Adapter adapterGroup;
    LinearLayoutManager mLayoutManager;

    private LinkedHashMap<Item,ArrayList<Item>> groupList;

    private final static int REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        //editCreateGroup=(EditText)findViewById(R.id.editCreateGroup);
        btnCreateGroup=(FloatingActionButton) findViewById(R.id.CreateGroup);

        //groupList=new ArrayList<>();
        groupList = new LinkedHashMap<Item,ArrayList<Item>>();

        groupList=initContactList();

        recycleGroup=(RecyclerView)findViewById(R.id.recycleGroup);
        adapterGroup=new AdapterGroup(groupList,getApplication());
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
                Intent i=new Intent(GroupActivity.this,ContactBook.class);
                startActivityForResult(i,REQUEST_CODE);
                //Toast.makeText(MainActivity.this, ""+groupList.get(0).toString(), Toast.LENGTH_SHORT).show();
            }
        });

        recycleGroup.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplication(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                        Intent i=new Intent(GroupActivity.this,ContactBook.class);
                        startActivityForResult(i,REQUEST_CODE);

                        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                })
        );


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode != 0) {

            switch (resultCode) {
                case ContactBook.RESULT_SCHEDULED:
                    SharedPreferences contactP = getSharedPreferences("contactP", 0);
                    int size1 = contactP.getInt("size", 0);
                    for (int j = 0; j < size1; j++) {
                        String s1 = contactP.getString("val" + j, "");
                        int s2=getRawContactId(Integer.parseInt(s1));
                        //addToGroup(s2,1);
                        }
                    break;

            }
        }


    }
    public int getRawContactId(int contactId) {
        String[] projection = new String[]{ContactsContract.RawContacts._ID};
        String selection = ContactsContract.RawContacts.CONTACT_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(contactId)};
        Cursor c = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
        c.moveToNext();
        int rawContactId = c.getInt(c.getColumnIndex(ContactsContract.RawContacts._ID));
        return rawContactId;
    }

    public void addToGroup(long personId, long groupId) {

        //remove if exists
        //this.removeFromGroup(personId, groupId);

        ContentValues values = new ContentValues();
        values.put(ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID,
                personId);
        values.put(
                ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,
                groupId);
        values
                .put(
                        ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE,
                        ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE);

         getContentResolver().insert(
                ContactsContract.Data.CONTENT_URI, values);

    }
    private void createGroup() {

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Groups.CONTENT_URI)
                .withValue(
                        ContactsContract.Groups.TITLE,
                        "")
                .withValue(
                        ContactsContract.Groups.ACCOUNT_TYPE,
                        GROUP_TYPE)
                .withValue(
                        ContactsContract.Groups.ACCOUNT_NAME,
                        "")
                .build());
        try {

            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
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
                groupTitle.add(groupName);
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
            //item.name = item.name+" ("+groupMembers.size()+")";
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
