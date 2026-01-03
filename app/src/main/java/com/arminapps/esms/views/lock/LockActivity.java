package com.arminapps.esms.views.lock;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.arminapps.esms.R;
import com.arminapps.esms.databinding.ActivityLockBinding;
import com.arminapps.esms.utils.SessionManager;
import com.arminapps.esms.views.main.MainActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;

public class LockActivity extends AppCompatActivity {

    private ActivityLockBinding binding;
    private SessionManager session;
    private final int RESTRICTION_SECONDS = 20;
    private final int MAX_FAILS = 3;
    private int currentFails = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        session = new SessionManager(this);
        currentFails = session.getInt("current_fails");
        currentFails = currentFails == -1 ? 0 : currentFails;

        if (session.getBoolean("is_restricted")) {
            if (new Date().getTime() < session.getLong("restricted_until")) {
                binding.txtPasscodeContainer.setVisibility(GONE);
                binding.txtLockMessage.setText("Your access to eSMS has been restricted due to numerous failed login attempts. Try again later.");
                startUnlockingWatchdog();
            }
            else {
                binding.txtPasscodeContainer.setVisibility(VISIBLE);
                binding.txtLockMessage.setText("Enter the passcode to unlock eSMS.");
                currentFails = 0;
                session.setInt("current_fails", -1);
            }
        }

        binding.txtPasscodeContainer.setEndIconOnClickListener(v -> {
            if (binding.txtPasscode.getText().toString().equals(session.getString("passcode"))) {
                session.setInt("current_fails", -1);
                startActivity(new Intent(LockActivity.this, MainActivity.class));
                finish();
            }
            else {
                if ((MAX_FAILS - (++currentFails) > 0))
                    Snackbar.make(binding.getRoot(), "Password is incorrect. Remaining login attempts: " + (MAX_FAILS - currentFails), Snackbar.LENGTH_LONG).show();
                binding.txtPasscode.getText().clear();
                session.setInt("current_fails", currentFails);
                if (currentFails == MAX_FAILS) {
                    session.setBoolean("is_restricted", true);
                    session.setLong("restricted_until", new Date().getTime() + (RESTRICTION_SECONDS * 1000));
                    binding.txtPasscodeContainer.setVisibility(GONE);
                    binding.txtLockMessage.setText("Your access to eSMS has been restricted due to numerous failed login attempts. Try again later.");
                    startUnlockingWatchdog();
                }
            }
        });
    }

    private void startUnlockingWatchdog() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (new Date().getTime() > session.getLong("restricted_until")) {
                    session.setBoolean("is_restricted", false);
                    session.setLong("restricted_until", -1);
                    binding.txtLockMessage.setText("Enter the passcode to unlock eSMS.");
                    binding.txtPasscodeContainer.setVisibility(VISIBLE);
                    currentFails = 0;
                    session.setInt("current_fails", -1);
                }
                else
                    handler.postDelayed(this, 1000);
            }
        }, 1000);
    }
}