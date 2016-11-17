package com.apps.OneWindowSol.onewindowsms2.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apps.OneWindowSol.onewindowsms2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private final static int REQUEST_CODE = 1;
    private String deviceId;
    EditText editText,editText1;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        editText=(EditText)findViewById(R.id.input_username);
        editText1=(EditText)findViewById(R.id.input_password);
        btn=(Button)findViewById(R.id.btn_login);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        TelephonyManager tm = ( TelephonyManager ) getSystemService(Context.TELEPHONY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //User has previously accepted this permission
            if (ActivityCompat.checkSelfPermission(Register.this,
                    Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                deviceId =  tm.getDeviceId();
            }
        } else {
            //Not in api-23, no need to prompt
            deviceId =  tm.getDeviceId();
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(editText.getText().toString())){
                    editText.setError("username is empty");
                }else if(TextUtils.isEmpty(editText1.getText().toString())){
                    editText1.setError("password is empty");
                }else{
                    getBlock(deviceId,editText.getText().toString(),editText1.getText().toString());
                }


            }
        });

    }
    final String[] block = new String[1];
    final String[] duration1 = new String[1];
    public void getBlock(final String device, final String username,final String password){
        String url="http://www.waseemafridient.com/OWS/block1.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //JSONObject jsonResponse = new JSONObject(response).getJSONObject("block");
                            block[0] =new JSONObject(response).getString("block");

                            duration1[0] =new JSONObject(response).getString("duration");

                            Intent i=new Intent(Register.this,MainActivity.class);
                            startActivity(i);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                // the POST parameters:
                params.put("deviceId", device);
                params.put("user_name",username);
                params.put("pass",password);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(Register.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Register.this,
                    Manifest.permission.READ_PHONE_STATE)) {

                // Show an expanation to the user asynchronously -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //  TODO: Prompt with explanation!

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(Register.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(Register.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    if (ActivityCompat.checkSelfPermission(Register.this,
                            Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(Register.this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode != 0) {
            int messageId;
            switch (resultCode) {
                case AddSmsActivity.RESULT_SCHEDULED:
                    messageId = R.string.successfully_scheduled;
                    break;
                case AddSmsActivity.RESULT_UNSCHEDULED:
                    messageId = R.string.successfully_unscheduled;
                    break;
                default:
                    messageId = R.string.error_generic;
                    System.out.println("Unknown AddSmsActivity result code: " + resultCode);
                    break;
            }
            //Toast.makeText(getApplicationContext(), getString(messageId), Toast.LENGTH_SHORT).show();
        }
    }
}
