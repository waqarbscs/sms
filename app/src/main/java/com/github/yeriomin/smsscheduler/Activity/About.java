package com.github.yeriomin.smsscheduler.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.github.yeriomin.smsscheduler.R;

public class About extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        webView=(WebView)findViewById(R.id.textView);
        webView.loadUrl("file:///android_asset/www/about.html");
    }
}
