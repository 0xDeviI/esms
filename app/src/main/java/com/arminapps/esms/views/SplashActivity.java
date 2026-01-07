package com.arminapps.esms.views;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.arminapps.esms.utils.SessionManager;
import com.arminapps.esms.views.lock.LockActivity;
import com.arminapps.esms.views.main.MainActivity;
import com.arminapps.esms.views.setup.SetupActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);

        if (session.getBoolean("setup_complete"))
            startActivity(new Intent(
                    SplashActivity.this,
                    LockActivity.class
            ));
        else
            startActivity(new Intent(
                    SplashActivity.this,
                    SetupActivity.class
            ));

        finish();
    }
}