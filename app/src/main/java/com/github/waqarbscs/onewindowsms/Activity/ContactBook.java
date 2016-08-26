package com.github.waqarbscs.onewindowsms.Activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.github.waqarbscs.onewindowsms.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContactBook extends AppCompatActivity {

    ArrayList<SelectUser> selectUsers;
    List<SelectUser> temp;
    // Contact List
    ListView listView;
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

    int l=0;

    final private static int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    final private String[] permissionsRequired = new String[] {
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CONTACTS
    };
    private ArrayList<String> permissionsGranted = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_book);

        getSupportActionBar().hide();


            selectUsers = new ArrayList<SelectUser>();
            resolver = this.getContentResolver();
            listView = (ListView) findViewById(R.id.contacts_list);
            listView.setFastScrollEnabled(false);

            btnDone = (Button) findViewById(R.id.done);


            phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            LoadContact loadContact = new LoadContact();
            loadContact.execute();
            totalNum = phones.getCount();


            checkAll = (CheckBox) findViewById(R.id.checkAll);
            numCount = (TextView) findViewById(R.id.numCount);

            checkAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkAll.isChecked()) {
                        adapter = new SelectUserAdapter(selectUsers, ContactBook.this, numCount, totalNum, checkAll);
                        listView.setAdapter(adapter);
                    } else {
                        adapter = new SelectUserAdapter(selectUsers, ContactBook.this, numCount, totalNum, checkAll);
                        listView.setAdapter(adapter);
                        //LoadContact loadContact = new LoadContact();
                        //   loadContact.execute();

                    }
                }
            });

            btnDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    back = true;
                    LoadContact loadContact = new LoadContact();
                    loadContact.execute();

                }
            });


            search = (SearchView) findViewById(R.id.searchView);
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
                    return false;
                }
            });
    }
    // Load data on background
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

                while (phones.moveToNext()) {
                    Bitmap bit_thumb = null;
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
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
                    if(!back)
                    selectUser.setCheckedBox(false);
                    selectUsers.add(selectUser);


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
            adapter = new SelectUserAdapter(selectUsers, ContactBook.this, numCount, totalNum, checkAll);
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

                SharedPreferences phoneP = getSharedPreferences("phoneP", 0);
                SharedPreferences.Editor phoneE = phoneP.edit();
                for (int k = 0, p = 1,s=0; k < phones.getCount(); k++) {
                        try {
                            SelectUser data = selectUsers.get(k);
                            //data.getCheckedBox();
                            if (data.getCheckedBox()) {
                                phoneE.putString("val" + s, data.getPhone());
                                s++;
                                phoneE.putInt("size", p++);
                                phoneE.commit();
                            } else {
                                phoneP.edit().remove("val" + k).commit();
                            }
                        }catch (Exception ex){
                            //Toast.makeText(ContactBook.this, "adsa", Toast.LENGTH_SHORT).show();
                        }
                }
                SharedPreferences numP = getSharedPreferences("numP", 0);
                SharedPreferences.Editor numE = numP.edit();
                for (int k = 0, p = 1,s=0; k < phones.getCount(); k++) {
                        try {
                            SelectUser data = selectUsers.get(k);
                            //data.getCheckedBox();
                            if (data.getCheckedBox()) {
                                numE.putString("val" + s, data.getName());
                                s++;
                                numE.putInt("size", p++);
                                numE.commit();
                            } else {
                                numP.edit().remove("val" + k).commit();
                            }
                        }catch (IndexOutOfBoundsException ex)
                        {
                            //Toast.makeText(ContactBook.this, "adsa", Toast.LENGTH_SHORT).show();
                        }
                    }
                Intent returnIntent = new Intent();
                setResult(RESULT_SCHEDULED, returnIntent);
                finish();

            }
                //listView.setFastScrollEnabled(true);
                numCount.setText(checkedNum + "/" + totalNum + " selected");
            }
    }
    final public static int RESULT_SCHEDULED = 1;
    final public static int RESULT_UNSCHEDULED = 2;
    boolean back=false;
    @Override
    public void onBackPressed() {
        //back=true;
        //LoadContact loadContact = new LoadContact();
        //loadContact.execute();
        super.onBackPressed();
        finish();

    }

    @Override
    protected void onStop() {
        super.onStop();
        phones.close();
    }
    private boolean permissionsGranted() {
        boolean granted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissionsNotGranted = new ArrayList<>();
            for (String required : this.permissionsRequired) {
                if (checkSelfPermission(required) != PackageManager.PERMISSION_GRANTED) {
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
