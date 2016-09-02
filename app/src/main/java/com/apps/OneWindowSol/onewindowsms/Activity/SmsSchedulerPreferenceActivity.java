package com.apps.OneWindowSol.onewindowsms.Activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.github.waqarbscs.onewindowsms.R;

public class SmsSchedulerPreferenceActivity extends PreferenceActivity {

    public static final String PREFERENCE_DELIVERY_REPORTS = "prefSendDeliveryReport";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}