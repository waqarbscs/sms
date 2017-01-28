package com.apps.OneWindowSol.onewindowsms2.Views;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.OneWindowSol.onewindowsms2.Activity.SelectUser;
import com.apps.OneWindowSol.onewindowsms2.Activity.SelectUserAdapter;
import com.apps.OneWindowSol.onewindowsms2.Group.Item;
import com.apps.OneWindowSol.onewindowsms2.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Contacts extends Fragment implements ContactGroup.Communication {

    private Context context;

    ArrayList<SelectUser> selectUsers;
    // Contact List
    ListView listView;
    List<String> name,phone,contactId;
    // Cursor to load contacts list
    Cursor phones, email;

    // Pop up
    ContentResolver resolver;
    SearchView search;
    SelectUserAdapter adapter;

    CheckBox checkAll;
    TextView numCount;
    int totalNum,checkedNum=0;

    Button btnDone;

    private ProgressDialog waitDialog;

    final private static int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    final private String[] permissionsRequired = new String[] {
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CONTACTS
    };
    private ArrayList<String> permissionsGranted = new ArrayList<>();


    public void SetContext(Context rContext) {
        context = rContext;
    }

    public Contacts() {
        // Required empty public constructor
    }

    public void getData(List<Item> aa){
//        Toast.makeText(context, aa.toString(), Toast.LENGTH_SHORT).show();
        for(int i=0;i<selectUsers.size();i++){
            for(int j=0;j<aa.size();j++) {
                if ((selectUsers.get(i).getName().equals(aa.get(j).name))  || (selectUsers.get(i).getName().equals(aa.get(j).name))) {
                    selectUsers.get(i).setCheckedBox(true);
                    name.add(selectUsers.get(i).getName());
                    phone.add(selectUsers.get(i).getPhone());
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
        }
        numCount.setText(aa.size()+"/"+totalNum+" selected");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View parentView=inflater.inflate(R.layout.fragment_contacts, container, false);
        waitDialog=new ProgressDialog(getActivity());
        waitDialog.setMessage("contact loading");

        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.setCancelable(false);
        selectUsers = new ArrayList<SelectUser>();

        name=new ArrayList<>();
        phone=new ArrayList<>();
        contactId=new ArrayList<>();

        resolver = getActivity().getContentResolver();
        listView = (ListView)parentView.findViewById(R.id.contacts_list);
        listView.setFastScrollEnabled(false);

        btnDone = (Button)parentView.findViewById(R.id.done);

        phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        LoadContact loadContact = new LoadContact();
        loadContact.execute();
        totalNum = phones.getCount();

        checkAll = (CheckBox)parentView.findViewById(R.id.checkAll);
        numCount = (TextView)parentView.findViewById(R.id.numCount);
        checkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phones.moveToFirst();

                if (checkAll.isChecked()) {
                    while(!phones.isAfterLast()){
                        name.add(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                        phone.add(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        contactId.add(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
                        phones.moveToNext();
                    }
                    //adapter = new SelectUserAdapter(selectUsers, ContactBook.this, numCount, totalNum, checkAll,phone,name);
                    //listView.setAdapter(adapter);
                    for(int i=0;i<selectUsers.size();i++){
                        selectUsers.get(i).setCheckedBox(true);
                    }
                    checkedNum=totalNum;
                    numCount.setText(checkedNum+"/"+totalNum+" selected");
                    adapter.notifyDataSetChanged();
                } else {
                    //adapter = new SelectUserAdapter(selectUsers, ContactBook.this, numCount, totalNum, checkAll,phone,name );
                    //listView.setAdapter(adapter);
                    for(int i=0;i<selectUsers.size();i++){
                        selectUsers.get(i).setCheckedBox(false);
                    }
                    numCount.setText(checkedNum+"/"+totalNum+" selected");
                    adapter.notifyDataSetChanged();
                    name.clear();
                    phone.clear();
                    contactId.clear();
                    //LoadContact loadContact = new LoadContact();
                    //   loadContact.execute();
                }
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back = true;

                waitDialog.show();
                // LoadContact loadContact = new LoadContact();
                // loadContact.execute();
                Thread backgroundThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // keep sure that this operations
                        // are thread-safe!
                        /*
                        SharedPreferences phoneP = getActivity().getSharedPreferences("phoneP", 0);
                        SharedPreferences.Editor phoneE = phoneP.edit();

                        for (int k = 0, p = 1,s=0; k < name.size(); k++) {
                            try {
                                // SelectUser data = selectUsers.get(k);
                                //data.getCheckedBox();
                                phoneE.putString("val" + s, phone.get(k));
                                s++;
                                phoneE.putInt("size", p++);
                                phoneE.commit();

                            //if (data.getCheckedBox()) {
                             //   phoneE.putString("val" + s, data.getPhone());
                             //   s++;
                             //   phoneE.putInt("size", p++);
                             //   phoneE.commit();
                            //} else {
                            //    phoneP.edit().remove("val" + k).commit();
                            //}

                            } catch (Exception ex) {
                                //Toast.makeText(ContactBook.this, "adsa", Toast.LENGTH_SHORT).show();
                            }
                        }

                        SharedPreferences numP = getActivity().getSharedPreferences("numP", 0);
                        SharedPreferences.Editor numE = numP.edit();

                        for (int k = 0, p = 1, s = 0; k < name.size(); k++) {
                            try {
                                SelectUser data = selectUsers.get(k);
                                numE.putString("val" + s, name.get(k));
                                s++;
                                numE.putInt("size", p++);
                                numE.commit();
                                //data.getCheckedBox();

                            //if (data.getCheckedBox()) {
                             //   numE.putString("val" + s, name.get(k));
                             //   s++;
                             //   numE.putInt("size", p++);
                             //   numE.commit();
                            //} else {
                            //    numP.edit().remove("val" + k).commit();
                            //}

                            } catch (IndexOutOfBoundsException ex) {
                                //Toast.makeText(ContactBook.this, "adsa", Toast.LENGTH_SHORT).show();
                            }
                        }

                        SharedPreferences contactP = getActivity().getSharedPreferences("contactP", 0);
                        SharedPreferences.Editor contactE = contactP.edit();

                        for (int k = 0, p = 1, s = 0; k < name.size(); k++) {
                            try {
                                //SelectUser data = selectUsers.get(k);
                                contactE.putString("val" + s, contactId.get(k));
                                s++;
                                contactE.putInt("size", p++);
                                contactE.commit();
                                //data.getCheckedBox();

                            //if (data.getCheckedBox()) {
                            //    numE.putString("val" + s, name.get(k));
                            //    s++;
                            //    numE.putInt("size", p++);
                            //    numE.commit();
                            //} else {
                            //    numP.edit().remove("val" + k).commit();
                            //}

                            } catch (IndexOutOfBoundsException ex) {
                                //Toast.makeText(ContactBook.this, "adsa", Toast.LENGTH_SHORT).show();
                            }
                        }
                        */
                        Intent returnIntent = new Intent();
                        returnIntent.putStringArrayListExtra("name", (ArrayList<String>) name);
                        returnIntent.putStringArrayListExtra("phone", (ArrayList<String>) phone);
                        getActivity().setResult(RESULT_SCHEDULED, returnIntent);
                        getActivity().finish();


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                waitDialog.dismiss();

                            }
                        });
                    }
                });
                backgroundThread.start();


            }
        });


        search = (SearchView)parentView.findViewById(R.id.searchView);
        search.setIconifiedByDefault(false);
        search.setQueryHint("Search");
        //*** setOnQueryTextListener ***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                if (phones != null) {
                    totalNum = phones.getCount();
                }
                adapter.filter(newText);
                listView.invalidate();
                return false;
            }
        });


        return parentView;
    }

    @Override
    public void setMnp(List<Item> aa) {
        List<Item> contacts=aa;
    }

    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

            if (phones != null) {
                //Log.e("count", "" + phones.getCount());

                if (phones.getCount() == 0) {
                    //t.makeText(ContactBook.this, "No contacts in your contact list.", Toast.LENGTH_LONG).show();
                }
                List<String> Snumber=new ArrayList<>();
                while (phones.moveToNext()) {
                    if ( !Snumber.contains(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s+",""))) {
                        Bitmap bit_thumb = null;
                        String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                        String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Snumber.add(phoneNumber.replaceAll("\\s+",""));
                        String EmailAddr = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA2));
                        String image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                        try {
                            if (image_thumb != null) {
                                bit_thumb = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(image_thumb));
                            } else {
                                Log.e("No Image Thumb", "--------------");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        SelectUser selectUser = new SelectUser();
                        selectUser.setThumb(bit_thumb);
                        selectUser.setName(name);
                        selectUser.setPhone(phoneNumber);
                        selectUser.setEmail(id);
                        if (!back)
                            selectUser.setCheckedBox(false);
                        selectUsers.add(selectUser);
                    }
                }

            } else {
                Log.e("Cursor close 1", "----------------");
            }
            //phones.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new SelectUserAdapter(selectUsers, getActivity(), numCount, totalNum, checkAll,phone,name,checkedNum,contactId);
            listView.setAdapter(adapter);

            // Select item on listclick
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Log.e("search", "here---------------- listener");

                    SelectUser data = selectUsers.get(i);
                }
            });
            if (back) {


                SharedPreferences phoneP = getActivity().getSharedPreferences("phoneP", 0);
                SharedPreferences.Editor phoneE = phoneP.edit();

                for (int k = 0, p = 1,s=0; k < name.size(); k++) {
                    try {
                        // SelectUser data = selectUsers.get(k);
                        //data.getCheckedBox();
                        phoneE.putString("val" + s, phone.get(k));
                        s++;
                        phoneE.putInt("size", p++);
                        phoneE.commit();
                            /*
                            if (data.getCheckedBox()) {
                                phoneE.putString("val" + s, data.getPhone());
                                s++;
                                phoneE.putInt("size", p++);
                                phoneE.commit();
                            } else {
                                phoneP.edit().remove("val" + k).commit();
                            }
                            */
                    } catch (Exception ex) {
                        //Toast.makeText(ContactBook.this, "adsa", Toast.LENGTH_SHORT).show();
                    }
                }

                SharedPreferences numP = getActivity().getSharedPreferences("numP", 0);
                SharedPreferences.Editor numE = numP.edit();

                for (int k = 0, p = 1, s = 0; k < name.size(); k++) {
                    try {
                        SelectUser data = selectUsers.get(k);
                        numE.putString("val" + s, name.get(k));
                        s++;
                        numE.putInt("size", p++);
                        numE.commit();
                        //data.getCheckedBox();
                            /*
                            if (data.getCheckedBox()) {
                                numE.putString("val" + s, name.get(k));
                                s++;
                                numE.putInt("size", p++);
                                numE.commit();
                            } else {
                                numP.edit().remove("val" + k).commit();
                            }
                            */
                    } catch (IndexOutOfBoundsException ex) {
                        //Toast.makeText(ContactBook.this, "adsa", Toast.LENGTH_SHORT).show();
                    }
                }

                SharedPreferences contactP = getActivity().getSharedPreferences("contactP", 0);
                SharedPreferences.Editor contactE = contactP.edit();

                for (int k = 0, p = 1, s = 0; k < name.size(); k++) {
                    try {
                        //SelectUser data = selectUsers.get(k);
                        contactE.putString("val" + s, contactId.get(k));
                        s++;
                        contactE.putInt("size", p++);
                        contactE.commit();
                        //data.getCheckedBox();
                            /*
                            if (data.getCheckedBox()) {
                                numE.putString("val" + s, name.get(k));
                                s++;
                                numE.putInt("size", p++);
                                numE.commit();
                            } else {
                                numP.edit().remove("val" + k).commit();
                            }
                            */
                    } catch (IndexOutOfBoundsException ex) {
                        //Toast.makeText(ContactBook.this, "adsa", Toast.LENGTH_SHORT).show();
                    }
                }

                Intent returnIntent = new Intent();
                getActivity().setResult(RESULT_SCHEDULED, returnIntent);
                ((Activity)context).finish();
                waitDialog.dismiss();
            }

            //listView.setFastScrollEnabled(true);
            numCount.setText(checkedNum + "/" + totalNum + " selected");
        }
    }
    final public static int RESULT_SCHEDULED = 1;
    final public static int RESULT_UNSCHEDULED = 2;
    boolean back=false;
    /*
    @Override
    public void onBackPressed() {
        //back=true;
        //LoadContact loadContact = new LoadContact();
        //loadContact.execute();
        super.onBackPressed();
        finish();
    }
    */

    @Override
    public void onStop() {
        super.onStop();
        phones.close();
    }
    private boolean permissionsGranted() {
        boolean granted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissionsNotGranted = new ArrayList<>();
            for (String required : this.permissionsRequired) {
                if (getActivity().checkSelfPermission(required) != PackageManager.PERMISSION_GRANTED) {
                    permissionsNotGranted.add(required);
                } else {
                    this.permissionsGranted.add(required);
                }
            }
            if (permissionsNotGranted.size() > 0) {
                granted = false;
                String[] notGrantedArray = permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]);
                requestPermissions(notGrantedArray, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
        }
        return granted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                List<String> requiredPermissions = Arrays.asList(this.permissionsRequired);
                String permission;
                for (int i = 0; i < permissions.length; i++) {
                    permission = permissions[i];
                    if (requiredPermissions.contains(permission)
                            && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        this.permissionsGranted.add(permission);
                    }
                }
                if (this.permissionsGranted.size() == this.permissionsRequired.length) {
                    //buildForm();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
