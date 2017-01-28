package com.apps.OneWindowSol.onewindowsms2.Views;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.apps.OneWindowSol.onewindowsms2.Activity.RecyclerItemClickListener;
import com.apps.OneWindowSol.onewindowsms2.Group.AdapterGroup;
import com.apps.OneWindowSol.onewindowsms2.Group.Item;
import com.apps.OneWindowSol.onewindowsms2.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactGroup extends Fragment {


    private Context context;

    List<Item> groups;
    List<ArrayList<Item>> groupContacts;
    ArrayList<String> num,name;

    private static final String GROUP_TYPE="com.onewindowsol.contactsgroup";

    private RecyclerView recycleGroup;
    private RecyclerView.Adapter adapterGroup;
    LinearLayoutManager mLayoutManager;

    private LinkedHashMap<Item,ArrayList<Item>> groupList;


    private ProgressDialog waitDialog;

    public void SetContext(Context rContext) {
        context = rContext;
    }

    final public static int RESULT_SCHEDULED = 1;
    final public static int RESULT_UNSCHEDULED = 2;

    Communication communication;

    public interface Communication{
          void setMnp(List<Item> aa);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            communication=(Communication)activity;
        }
        catch (ClassCastException e){
            Toast.makeText(activity, "fsdfdsfdf", Toast.LENGTH_SHORT).show();
        }
    }

    public ContactGroup() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View parentView=inflater.inflate(R.layout.fragment_contact_group, container, false);

        waitDialog=new ProgressDialog(getActivity());
        waitDialog.setMessage("contact loading");

        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.setCancelable(false);

        groupList = new LinkedHashMap<Item,ArrayList<Item>>();

        groupList=initContactList();

        num=new ArrayList<>();
        name=new ArrayList<>();


        groups=new ArrayList<>(groupList.keySet());
        groupContacts=new ArrayList<>(groupList.values());

        recycleGroup=(RecyclerView)parentView.findViewById(R.id.recycleGroup);
        adapterGroup=new AdapterGroup(groupList,groups,getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity());

        mLayoutManager = new LinearLayoutManager(context);

        recycleGroup.setAdapter(adapterGroup);
        recycleGroup.setLayoutManager(mLayoutManager);




        recycleGroup.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(),recycleGroup, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        /*
                        Intent returnIntent = new Intent();
                        ArrayList<Item> data=groupList.get(groups.get(position));
                        for(int i=0;i<data.size();i++){
                            num.add(data.get(i).phNo);
                            name.add(data.get(i).name);
                        }
                        returnIntent.putStringArrayListExtra("name",name);
                        returnIntent.putStringArrayListExtra("num",num);
                        getActivity().setResult(RESULT_SCHEDULED, returnIntent);
                        getActivity().finish();
                        */
                        ViewPager viewPager=(ViewPager)getActivity().findViewById(R.id.viewpager);
                        viewPager.setCurrentItem(1);
                        communication.setMnp(groupContacts.get(position));
                        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        ViewPager viewPager=(ViewPager)getActivity().findViewById(R.id.viewpager);
                        viewPager.setCurrentItem(1);
                        communication.setMnp(groupContacts.get(position));
                    }
                })
            );

        return parentView;
    }

    private ArrayList<Item> fetchGroups(){
        ArrayList<Item> groupList = new ArrayList<Item>();
        String[] projection = new String[]{ContactsContract.Groups._ID,ContactsContract.Groups.TITLE};
        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Groups.CONTENT_URI,
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
            item.name = item.name +" ("+groupMembers.size()+")";
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
        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection, where,null,
                ContactsContract.Data.DISPLAY_NAME+" COLLATE LOCALIZED ASC");
        while(cursor.moveToNext()){
            Item item = new Item();
            item.name = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            item.id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID));
            Cursor phoneFetchCursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
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
