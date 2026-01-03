package com.arminapps.esms.views.setup;

import static android.Manifest.permission.READ_CONTACTS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.arminapps.esms.R;
import com.arminapps.esms.data.db.AppDatabase;
import com.arminapps.esms.data.db.DAOs.ContactDAO;
import com.arminapps.esms.data.models.Contact;
import com.arminapps.esms.databinding.ActivitySetupBinding;
import com.arminapps.esms.utils.ContactHelper;
import com.arminapps.esms.utils.SessionManager;
import com.arminapps.esms.views.main.MainActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;

public class SetupActivity extends AppCompatActivity {

    private ActivitySetupBinding binding;
    private SessionManager session;
    private enum LoadingStep {
        SETTING_PASSWORD,
        SAVING_CONTACTS,
        FINISHING
    }
    private final static int CONTACT_READ_REQUEST_CODE = 0x404;
    private AppDatabase database;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setup);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        session = new SessionManager(this);
        database = AppDatabase.getInstance(getApplicationContext());
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted)
                importContacts();
            else
                Snackbar.make(binding.getRoot(), "Contacts auto-import rejected. You may import them later.", Snackbar.LENGTH_LONG).show();
            loading(LoadingStep.FINISHING);
        });

        binding.btnPasscodeConfirm.setOnClickListener(v -> {
            if (!binding.txtPasscode.getText().toString().isEmpty())
                loading(LoadingStep.SETTING_PASSWORD);
            else
                Snackbar.make(binding.getRoot(), "Enter the passcode.", Snackbar.LENGTH_LONG).show();
        });
    }

    private void loading(LoadingStep loadingStep) {
        binding.setupView.setVisibility(GONE);
        binding.loadingView.setVisibility(VISIBLE);
        if (loadingStep == LoadingStep.SETTING_PASSWORD) {
            binding.txtLoading.setText("Setting passcode ...");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    session.setBoolean("setup_complete", true);
                    session.setString("passcode", binding.txtPasscode.getText().toString());
                    loading(LoadingStep.SAVING_CONTACTS);
                }
            }, 2000);
        }
        else if (loadingStep == LoadingStep.SAVING_CONTACTS) {
            binding.txtLoading.setText("Importing contacts ...");
            if (ContextCompat.checkSelfPermission(this, READ_CONTACTS) != PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(READ_CONTACTS);
            }
            else {
                importContacts();
                loading(LoadingStep.FINISHING);
            }
        }
        else if (loadingStep == LoadingStep.FINISHING) {
            binding.txtLoading.setText("Finalizing initialization ...");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(
                            SetupActivity.this,
                            MainActivity.class
                    ));
                    finish();
                }
            }, 2000);
        }
    }

    private void importContacts() {
        ContactDAO contactDAO = database.contactDAO();
        ArrayList<Contact> contacts = new ContactHelper().getContacts(getApplicationContext());
        for (var contact : contacts) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    contactDAO.insert(contact);
                }
            }).start();
        }
        session.setBoolean("contacts_imported", true);
    }
}