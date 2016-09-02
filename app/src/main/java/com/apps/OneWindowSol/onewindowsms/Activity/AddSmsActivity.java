package com.apps.OneWindowSol.onewindowsms.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.apps.OneWindowSol.onewindowsms.AlarmReceiver;
import com.apps.OneWindowSol.onewindowsms.DbHelper;
import com.github.waqarbscs.onewindowsms.R;
import com.apps.OneWindowSol.onewindowsms.SmsModel;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;


public class AddSmsActivity extends AppCompatActivity {

    final public static int RESULT_SCHEDULED = 1;
    final public static int RESULT_UNSCHEDULED = 2;

    String[] num=new String[100];
    String[] nam=new String[100];

    private final static int REQUEST_CODE = 3;

    TextView countMessageLength;

    AutoCompleteTextView formContact = null;

    int numCount=1;
    boolean click=false;

   // TextView cont;
    Button btnfind;

    final private static int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    final private String[] permissionsRequired = new String[] {
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CONTACTS
    };

    public void expandableButton1(View view) {
       // cont.setText("");
        //cont.setVisibility(View.GONE);
        click=true;

        Intent i=new Intent(AddSmsActivity.this,ContactBook.class);
        startActivityForResult(i,REQUEST_CODE);
    }

    private GregorianCalendar timeScheduled = new GregorianCalendar();
    private SmsModel sms;
    private ArrayList<String> permissionsGranted = new ArrayList<>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    String[] mn1,nm1;
    ImageButton expandable1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode != 0) {
            int messageId;
            switch (resultCode) {
                case ContactBook.RESULT_SCHEDULED:
                    num=new String[100];
                    messageId = R.string.successfully_scheduled;
                    SharedPreferences numP=getSharedPreferences("numP",0);
                    SharedPreferences phoneP = getSharedPreferences("phoneP", 0);
                    SharedPreferences.Editor numEditor=numP.edit();
                    SharedPreferences.Editor phoneEditor=phoneP.edit();
                    int size1=numP.getInt("size",0);
                    if(size1==0){
                        mn1=null;
                        nm1=null;
                    }
                    else {
                        mn1 = new String[size1];
                        nm1 = new String[size1];
                    }
                    for(int j=0;j<size1;j++) {
                        String s1 = phoneP.getString("val" + j, "");
                        //phoneList.add(phoneP.getString("val"+j,""));

                        if (j >= 0 && !s1.equals("")) {
                            if (!dataContactNumAdd.contains(s1)) {
                                // true
                                mn1[j] = numP.getString("val" + j, "");
                                //cont.append(mn1[j] + ",");
                                AddNameToList(numP.getString("val" + j, ""));
                                AddNumToList(phoneP.getString("val" + j, ""));
                            /*
                            if(!mn1[j].equals("")) {
                                //cont.setVisibility(View.VISIBLE);
                                cont.append(mn1[j] + ",");
                                //formContact.setEnabled(false);
                                dataContactNameAdd.add(s1);
                                int position1=dataContactNameAdd.size();
                                adapter.notifyItemInserted(position1-1);

                            }else{

                                cont.append(mn1[j]);
                                dataContactNameAdd.add(s1);
                                int position1=dataContactNameAdd.size();
                                adapter.notifyItemInserted(position1-1);
                            }
                            */
                            }
                        } else if (dataContactNameAdd.contains(s1)) {
                            mn1[j] = numP.getString("val" + j, "");
                            AddNameToList(numP.getString("val" + j, ""));
                            AddNumToList(phoneP.getString("val" + j, ""));
                            // cont.append(mn1[j] + ",");
                            /*
                            if(!mn1[j].equals("")) {
                                //cont.setVisibility(View.VISIBLE);
                                cont.append(mn1[j] + ",");
                                //formContact.setEnabled(false);
                                dataContactNameAdd.add(s1);
                                int position1=dataContactNameAdd.size();
                                adapter.notifyItemInserted(position1-1);

                            }else{

                                cont.append(mn1[j]);
                                dataContactNameAdd.add(s1);
                                int position1=dataContactNameAdd.size();
                                adapter.notifyItemInserted(position1-1);
                            }
                            */
                        }
                    }
                        numEditor.clear();
                        numEditor.commit();
                        phoneEditor.clear();
                        phoneEditor.commit();

                        //formContact.append(mn1[j]+",");

                    break;
                case ContactBook.RESULT_UNSCHEDULED:
                    messageId = R.string.successfully_unscheduled;
                    break;
                default:
                    messageId = R.string.error_generic;
                    System.out.println("Unknown AddSmsActivity result code: " + resultCode);
                    break;
            }
            //Toast.makeText(getApplicationContext(), "Internal error", Toast.LENGTH_SHORT).show();

            //Toast.makeText(getApplicationContext(), getString(messageId), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivityForResult(new Intent(this, SmsSchedulerPreferenceActivity.class), 1);
                break;
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(RESULT_SCHEDULED, returnIntent);
                finish();

        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (permissionsGranted()) {
            buildForm();
        }
    }
    int sy,sm,sd;
    private void buildForm() {
        timeScheduled = new GregorianCalendar();
        String recipient = "", message = "";

        /*
        cont=(TextView)findViewById(R.id.form_input_message1) ;
        cont.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mn1=null;
                cont.setText("");
                //cont.setVisibility(View.GONE);

                //formContact.setEnabled(true);
                return false;

            }
        });
        */

        //formContact.setTokenizer(new SpaceTokenizer());
        //formContact.setThreshold(1);
        final EditText formMessage = (EditText) findViewById(R.id.form_input_message);
        final Button formTime1 = (Button) findViewById(R.id.form_time1);
        final Button formDate1=(Button) findViewById(R.id.form_date1);

        final ImageButton formTime2 = (ImageButton) findViewById(R.id.i1);
        final ImageButton formDate2=(ImageButton) findViewById(R.id.i2);

        countMessageLength=(TextView)findViewById(R.id.countMessageLength);


        //final TimePicker formTime = (TimePicker) findViewById(R.id.form_time);
        //final DatePicker formDate = (DatePicker) findViewById(R.id.form_date);
        final Button buttonCancel = (Button) findViewById(R.id.button_cancel);

        // Filling form with data if provided
        if (sms.getTimestampCreated() > 0) {
            timeScheduled.setTimeInMillis(sms.getTimestampScheduled());
            recipient = sms.getRecipientName().length() > 0
                    //? getString(R.string.contact_format, sms.getRecipientName(), sms.getRecipientNumber())
                    ? dName
                    : dNumber
            ;
            message = sms.getMessage();
        }
        final Calendar c = Calendar.getInstance();
        final int mHour = c.get(Calendar.HOUR_OF_DAY);

        final int  mMinute = c.get(Calendar.MINUTE);

        final  int mYear=c.get(Calendar.YEAR);
        final  int mMonth=c.get(Calendar.MONTH);
        final  int mDate=c.get(Calendar.DATE);
        if(mHour<10&&mMinute<10){
            formTime1.setText("0"+timeScheduled.get(Calendar.HOUR_OF_DAY) + ":" + "0"+timeScheduled.get(Calendar.MINUTE));
        }else if(mHour<10&&mMinute>=10){
            formTime1.setText("0"+timeScheduled.get(Calendar.HOUR_OF_DAY) + ":" + timeScheduled.get(Calendar.MINUTE));
        }else if(mHour>=10&&mMinute<10){
            formTime1.setText(timeScheduled.get(Calendar.HOUR_OF_DAY) + ":" +"0"+ timeScheduled.get(Calendar.MINUTE));
        }else {
            formTime1.setText(timeScheduled.get(Calendar.HOUR_OF_DAY) + ":" + timeScheduled.get(Calendar.MINUTE));
        }

        if((timeScheduled.get(GregorianCalendar.MONTH)+1)<10&timeScheduled.get(GregorianCalendar.DAY_OF_MONTH)<10){
            formDate1.setText(timeScheduled.get(GregorianCalendar.YEAR)+"/0"+(timeScheduled.get(GregorianCalendar.MONTH)+1)+"/0"+timeScheduled.get(GregorianCalendar.DAY_OF_MONTH));
        }else if((timeScheduled.get(GregorianCalendar.MONTH)+1)<10&&timeScheduled.get(GregorianCalendar.DAY_OF_MONTH)>=10){
            formDate1.setText(timeScheduled.get(GregorianCalendar.YEAR)+"/0"+(timeScheduled.get(GregorianCalendar.MONTH)+1)+"/"+timeScheduled.get(GregorianCalendar.DAY_OF_MONTH));
        }else if((timeScheduled.get(GregorianCalendar.MONTH)+1)>=10&&timeScheduled.get(GregorianCalendar.DAY_OF_MONTH)<10){
            formDate1.setText(timeScheduled.get(GregorianCalendar.YEAR)+"/"+(timeScheduled.get(GregorianCalendar.MONTH)+1)+"/0"+timeScheduled.get(GregorianCalendar.DAY_OF_MONTH));
        }else {
            formDate1.setText(timeScheduled.get(GregorianCalendar.YEAR)+"/"+(timeScheduled.get(GregorianCalendar.MONTH)+1)+"/"+timeScheduled.get(GregorianCalendar.DAY_OF_MONTH));
        }
        //formDate1.setText(timeScheduled.get(GregorianCalendar.YEAR)+"/"+(timeScheduled.get(GregorianCalendar.MONTH)+1)+"/"+timeScheduled.get(GregorianCalendar.DAY_OF_MONTH));
        formDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(AddSmsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        int m=i1+1;
                        if(m<10&i2<10){
                            formDate1.setText(i+"/0"+m+"/0"+i2);
                        }else if(m<10&&i2>=10){
                            formDate1.setText(i+"/0"+m+"/"+i2);
                        }else if(m>=10&&i2<10){
                            formDate1.setText(i+"/"+m+"/0"+i2);
                        }else {
                            formDate1.setText(i+"/"+m+"/"+i2);
                        }

                        sy=i;sm=m;sd=i2;
                    }
                },mYear,mMonth,mDate);
                datePickerDialog.show();

            }
        });
        /*
        formTime.setIs24HourView(android.text.format.DateFormat.is24HourFormat(this));
        formTime.setCurrentHour(timeScheduled.get(Calendar.HOUR_OF_DAY));
        formTime.setCurrentMinute(timeScheduled.get(Calendar.MINUTE));
        formDate.updateDate(
                timeScheduled.get(GregorianCalendar.YEAR),
                timeScheduled.get(GregorianCalendar.MONTH),
                timeScheduled.get(GregorianCalendar.DAY_OF_MONTH)
        );
        */
        //formContact.append(recipient);
        //cont.setVisibility(View.VISIBLE);
            //cont.append(recipient);
        formMessage.append(message);
        /*
        buttonCancel.setVisibility(sms.getTimestampCreated() > 0 ? View.VISIBLE : View.GONE);
        int stringId = sms.getStatus().contentEquals(SmsModel.STATUS_PENDING)
                ? R.string.form_button_cancel
                : R.string.form_button_delete
                ;
        buttonCancel.setText(getString(stringId));
        */
        // Filling contacts list
        new Thread(new Runnable() {
            @Override
            public void run() {
                final SimpleAdapter adapter = new SimpleAdapter(
                        getApplicationContext(),
                        getContacts(),
                        R.layout.item_contact,
                        //null,new int[]{R.id.account_name, R.id.account_number});

                        new String[]{"Name", "Phone"},
                        new int[]{R.id.account_name, R.id.account_number}
                );

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        formContact.setAdapter(adapter);
                    }
                });
            }
        }).start();
        formContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap<String, String> recipient = (HashMap<String, String>) parent.getItemAtPosition(position);
                String name = recipient.get("Name"), phone = recipient.get("Phone");

                //formContact.append(name);



                if (!dataContactNumAdd.contains(phone)&&!dataContactNumAdd.contains(name)) {
                    // true
                    //cont.append(name+",");
                    num[numCount] = phone;
                    nam[numCount] = name;
                    numCount++;
                    AddNameToList(name);
                    AddNumToList(phone);
                }
               // cont.setVisibility(View.VISIBLE);

                formContact.setText("");



                //Toast.makeText(AddSmsActivity.this, name+":"+phone, Toast.LENGTH_SHORT).show();
                //sms.setRecipientName(name);
                //sms.setRecipientNumber(phone);
            }
        });

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!formContact.isPerformingCompletion()) {
                    sms.setRecipientName("");
                    sms.setRecipientNumber(String.valueOf(s));
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        formContact.addTextChangedListener(watcher);
        //cont.addTextChangedListener(watcher);
        // Adding emptiness checks
        TextWatcher watcherEmptiness = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                final Button button = (Button) findViewById(R.id.button_add);
                button.setEnabled(dataContactNameAdd.size()>0&&formMessage.getText().length() > 0);
            }
        };
        formContact.addTextChangedListener(watcherEmptiness);
        formMessage.addTextChangedListener(watcherEmptiness);
        TextWatcher watcherCount = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                countMessageLength.setText(String.valueOf(s.length())+"/140");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        formMessage.addTextChangedListener(watcherCount);
        // Adding time event listeners
        formTime2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog=new TimePickerDialog(AddSmsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        if(hourOfDay<10&&minute<10){
                            formTime1.setText("0"+hourOfDay + ":" + "0"+minute);
                        }else if(hourOfDay<10&&minute>=10){
                            formTime1.setText("0"+hourOfDay + ":" + minute);
                        }else if(hourOfDay>=10&&minute<10){
                            formTime1.setText(hourOfDay + ":" +"0"+ minute);
                        }else {
                            formTime1.setText(hourOfDay + ":" + minute);
                        }
                        timeScheduled.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay);
                        timeScheduled.set(GregorianCalendar.MINUTE, minute);

                    }
                },mHour,mMinute,false);
                timePickerDialog.show();
            }
        });
        /*
        formTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                timeScheduled.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay);
                timeScheduled.set(GregorianCalendar.MINUTE, minute);
            }
        });
        */
    }

    @Override
    protected void onPause() {
        super.onPause();
        DbHelper.closeDbHelper();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DbHelper.closeDbHelper();
    }

    List<String> dataContactNameAdd;
    List<String> dataContactNumAdd;

    String dName,dNumber;
    RecyclerView recyclerView;
    RecycleContactAdapter adapter;

    public void AddNameToList(String nam){
        int selectedItemPosition=dataContactNameAdd.size();
        dataContactNameAdd.add(nam);
        adapter.notifyItemInserted(selectedItemPosition);
    }
    public void AddNumToList(String num){
        int selectedItemPosition=dataContactNameAdd.size();
        dataContactNumAdd.add(num);
    }


    String item;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_sms);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setTitle("Shedule a SMS");
        dataContactNameAdd=new ArrayList<>();
        dataContactNumAdd=new ArrayList<>();
        List<String> data1=new ArrayList<>();

        expandable1=(ImageButton)findViewById(R.id.expandableButton1);
        formContact = (AutoCompleteTextView) findViewById(R.id.form_input_contact);
        btn=(Button)findViewById(R.id.button_add);

        long smsId = getSmsId(savedInstanceState);
        if(smsId>0){
            expandable1.setEnabled(false);
            formContact.setEnabled(false);
            btn.setText("Re-Shedule");
            btn.setEnabled(true);
            sms = DbHelper.getDbHelper(this).get(smsId);
            detail=true;
        }
       //data1.add("dsfdsf");
       // data1.add("sdfdsfds");
       // data1.add("sdafdsf");
       // data1.add("dsfdsf");
       // data1.add("sdfdsfds");
       // data1.add("sdafdsf");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new RecycleContactAdapter(dataContactNameAdd,this,dataContactNumAdd,detail,sms);
        recyclerView.setAdapter(adapter);
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        flowLayoutManager.setAutoMeasureEnabled(true);

        recyclerView.setLayoutManager(flowLayoutManager);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                item=adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        /**
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_example);

        // Add textview 1
        for(int i=0;i<size;i++) {
            TextView textView1 = new TextView(this);
            textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            textView1.setText(mn[i]);
            textView1.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB// in pixels (left, top, right, bottom)
            linearLayout.addView(textView1);
        }
         **/
        //Intent intent = new Intent(Intent.ACTION_PICK);
        //intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        //startActivityForResult(intent, 10011);
        // Filling existing sms info if possible

        if (smsId > 0) {
            sms = DbHelper.getDbHelper(this).get(smsId);
            Cursor cursor=DbHelper.getDbHelper(this).getname(Long.toString(smsId));
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                dName = cursor.getString(0);
                dNumber = cursor.getString(1);
                item=cursor.getString(2);

                AddNameToList(dName);
                AddNumToList(dNumber);


                int selectionPosition= adapter.getPosition(item);
                spinner.setSelection(selectionPosition);

                cursor.moveToNext();
            }
        } else {
            sms = new SmsModel();
        }

    }
    public void scheduleSms(View view) {
        //DatePicker formDate = (DatePicker) findViewById(R.id.form_date);
        Button formDate1 = (Button) findViewById(R.id.form_date1);
        Button formTime1 = (Button) findViewById(R.id.form_time1);

        int ab = Integer.parseInt(formDate1.getText().toString().substring(0, 4));
        int bc = Integer.parseInt(formDate1.getText().toString().substring(5, 7));
        int cd = Integer.parseInt(formDate1.getText().toString().substring(8, 10));
        int ra = Integer.parseInt(formTime1.getText().toString().substring(0, 2));
        int rb = Integer.parseInt(formTime1.getText().toString().substring(3, 5));
        timeScheduled.set(GregorianCalendar.YEAR, ab);
        timeScheduled.set(GregorianCalendar.MONTH, bc - 1);
        timeScheduled.set(GregorianCalendar.DAY_OF_MONTH, cd);
        timeScheduled.set(GregorianCalendar.HOUR_OF_DAY, ra);
        timeScheduled.set(GregorianCalendar.MINUTE, rb);
        if (timeScheduled.getTimeInMillis() < GregorianCalendar.getInstance().getTimeInMillis()) {
            Toast.makeText(getApplicationContext(), getString(R.string.form_validation_datetime), Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences phoneP = getSharedPreferences("phoneP", 0);
        SharedPreferences nameP = getSharedPreferences("numP", 0);
        int size = phoneP.getInt("size", 0);
        String[] mn = new String[size];
        String[] nm=new String[size];
        for (int j = 0; j < size; j++) {
            String s = phoneP.getString("val" + j, "");
            //phoneList.add(phoneP.getString("val"+j,""));
            mn[j] = phoneP.getString("val" + j, "");
            nm[j]=nameP.getString("val" + j, "");
        }
        int recursiveTime=0;
            if(item=="Once"){
                recursiveTime=1;
            }else if(item=="Daily"){
                recursiveTime=30;
            }else if(item=="Weekly"){
                recursiveTime=4;
            }else if(item=="Monthly"){
                recursiveTime=12;
            }
        if(!detail){

                for (int i = 0; i < dataContactNumAdd.size(); i++) {

                        sms.setRecipientName(dataContactNameAdd.get(i));
                        sms.setRecipientNumber(dataContactNumAdd.get(i));
                        sms.setTimestampScheduled(timeScheduled.getTimeInMillis());

                        EditText formMessage = (EditText) findViewById(R.id.form_input_message);
                        sms.setMessage(formMessage.getText().toString());

                        sms.setStatus(SmsModel.STATUS_PENDING);
                        sms.setRecursive(item);

                        DbHelper.getDbHelper(this).save(sms, "insert");
                    scheduleAlarm(sms);

            }
        }else {
            for (int i = 0; i < dataContactNumAdd.size(); i++) {
                sms.setRecipientName(dataContactNameAdd.get(i));
                sms.setRecipientNumber(dataContactNumAdd.get(i));
                sms.setTimestampScheduled(timeScheduled.getTimeInMillis());

                EditText formMessage = (EditText) findViewById(R.id.form_input_message);
                sms.setMessage(formMessage.getText().toString());

                sms.setStatus(SmsModel.STATUS_PENDING);
                sms.setRecursive(item);

                DbHelper.getDbHelper(this).save1(sms, "update",dataContactNumAdd.get(i));
            }
        }
        numCount = 0;


        Intent returnIntent = new Intent();
        setResult(RESULT_SCHEDULED, returnIntent);
        finish();
    }
    public void scheduleSms1(View view) {
        //DatePicker formDate = (DatePicker) findViewById(R.id.form_date);
        Button formDate1 = (Button) findViewById(R.id.form_date1);
        Button formTime1 = (Button) findViewById(R.id.form_time1);

        int ab = Integer.parseInt(formDate1.getText().toString().substring(0, 4));
        int bc = Integer.parseInt(formDate1.getText().toString().substring(5, 7));
        int cd = Integer.parseInt(formDate1.getText().toString().substring(8, 10));
        int ra = Integer.parseInt(formTime1.getText().toString().substring(0, 2));
        int rb = Integer.parseInt(formTime1.getText().toString().substring(3, 5));
        timeScheduled.set(GregorianCalendar.YEAR, ab);
        timeScheduled.set(GregorianCalendar.MONTH, bc - 1);
        timeScheduled.set(GregorianCalendar.DAY_OF_MONTH, cd);
        timeScheduled.set(GregorianCalendar.HOUR_OF_DAY, ra);
        timeScheduled.set(GregorianCalendar.MINUTE, rb);
        if (timeScheduled.getTimeInMillis() < GregorianCalendar.getInstance().getTimeInMillis()) {
            Toast.makeText(getApplicationContext(), getString(R.string.form_validation_datetime), Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences phoneP = getSharedPreferences("phoneP", 0);
        SharedPreferences nameP = getSharedPreferences("numP", 0);
        int size = phoneP.getInt("size", 0);
        String[] mn = new String[size];
        String[] nm=new String[size];
        for (int j = 0; j < size; j++) {
            String s = phoneP.getString("val" + j, "");
            //phoneList.add(phoneP.getString("val"+j,""));
            mn[j] = phoneP.getString("val" + j, "");
            nm[j]=nameP.getString("val" + j, "");
        }

        if(!detail){
        if (click) {
            for (int i = 0; i < size; i++) {

                sms.setRecipientName(nm[i]);
                sms.setRecipientNumber(mn[i]);
                sms.setTimestampScheduled(timeScheduled.getTimeInMillis());

                EditText formMessage = (EditText) findViewById(R.id.form_input_message);
                sms.setMessage(formMessage.getText().toString());

                sms.setStatus(SmsModel.STATUS_PENDING);

                DbHelper.getDbHelper(this).save(sms,"");
            }
        }else {
            for (int i = 0; i < numCount; i++) {

                sms.setRecipientName(nam[i]);
                sms.setRecipientNumber(num[i]);
                sms.setTimestampScheduled(timeScheduled.getTimeInMillis());

                EditText formMessage = (EditText) findViewById(R.id.form_input_message);
                sms.setMessage(formMessage.getText().toString());

                sms.setStatus(SmsModel.STATUS_PENDING);

                DbHelper.getDbHelper(this).save(sms,"");
            }
        }
        }else{
            sms.setRecipientName(dName);
            sms.setRecipientNumber(dNumber);
            sms.setTimestampScheduled(timeScheduled.getTimeInMillis());

            EditText formMessage = (EditText) findViewById(R.id.form_input_message);
            sms.setMessage(formMessage.getText().toString());

            sms.setStatus(SmsModel.STATUS_PENDING);

            DbHelper.getDbHelper(this).save(sms,"");
        }
            numCount = 0;
            scheduleAlarm(sms);

            Intent returnIntent = new Intent();
            setResult(RESULT_SCHEDULED, returnIntent);
            finish();
        }

    public void unscheduleSms(View view) {
        DbHelper.getDbHelper(this).delete(sms.getTimestampCreated());

        unscheduleAlarm(sms);

        Intent returnIntent = new Intent();
        setResult(RESULT_UNSCHEDULED, returnIntent);
        finish();
    }

    private List<? extends HashMap<String, ?>> getContacts() {
        ArrayList<HashMap<String, String>> contacts = new ArrayList<>();
        HashMap<String, String> names = new HashMap<>();

        // Getting contact names
        String[] projectionPeople = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };
        Cursor people = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                projectionPeople,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );
        if (null != people) {
            int columnIndexId = people.getColumnIndex(ContactsContract.Contacts._ID);
            int columnIndexName = people.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            while (people.moveToNext()) {
                names.put(people.getString(columnIndexId), people.getString(columnIndexName));
            }
            people.close();
        }

        // Getting phones
        String[] projectionPhones = new String[] {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        Cursor phones = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projectionPhones,
                null,
                null,
                null
        );
        if (null != phones) {
            int columnIndexId = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
            int columnIndexPhone = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            while (phones.moveToNext()) {
                String contactId = phones.getString(columnIndexId);
                String phoneNumber = phones.getString(columnIndexPhone);
                HashMap<String, String> NamePhoneType = new HashMap<>();
                NamePhoneType.put("Name", names.get(contactId));
                NamePhoneType.put("Phone", phoneNumber);
                contacts.add(NamePhoneType);
            }
            phones.close();
        }

        return contacts;
    }
    boolean detail=false;
    private long getSmsId(Bundle savedInstanceState) {
        String smsId = "0";
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                smsId = extras.getString(DbHelper.COLUMN_TIMESTAMP_CREATED);
                btn.setEnabled(true);
                detail=true;
            }
        } else {
            smsId = (String) savedInstanceState.getSerializable(DbHelper.COLUMN_TIMESTAMP_CREATED);
        }
        return Long.parseLong(smsId);
    }

    private void scheduleAlarm(SmsModel sms) {
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, sms.getTimestampScheduled(), getAlarmPendingIntent(sms));
    }

    private void unscheduleAlarm(SmsModel sms) {
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(getAlarmPendingIntent(sms));
    }
    
    private PendingIntent getAlarmPendingIntent(SmsModel sms) {
        Intent intent = new Intent(AlarmReceiver.INTENT_FILTER);
        intent.putExtra(DbHelper.COLUMN_TIMESTAMP_CREATED, sms.getTimestampCreated());
        return PendingIntent.getBroadcast(
                this,
                sms.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT & Intent.FILL_IN_DATA
        );
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
                    buildForm();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}