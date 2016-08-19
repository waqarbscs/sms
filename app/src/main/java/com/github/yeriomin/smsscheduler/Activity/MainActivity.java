package com.github.yeriomin.smsscheduler.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.yeriomin.smsscheduler.R;

public class MainActivity extends AppCompatActivity {
    Button btnNew,btnShow,btnDelete;
    private final static int REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        btnNew=(Button)findViewById(R.id.imageButton1);
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), AddSmsActivity.class), REQUEST_CODE);
            }
        });
        btnShow=(Button)findViewById(R.id.imageButton2);
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,Detail.class);
                startActivity(i);
            }
        });
        btnDelete=(Button)findViewById(R.id.imageButton3);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,Delete.class);
                startActivity(i);
            }
        });
        btnDelete=(Button)findViewById(R.id.imageButton4);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,About.class);
                startActivity(i);
            }
        });
    }
    public void gotoNextActivity(View view) {

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
