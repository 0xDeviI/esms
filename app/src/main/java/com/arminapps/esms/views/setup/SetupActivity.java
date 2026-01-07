package com.arminapps.esms.views.setup;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_MMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.SEND_SMS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SetupActivity extends AppCompatActivity {

    private ActivitySetupBinding binding;
    private SessionManager session;
    private enum LoadingStep {
        SETTING_PASSWORD,
        SAVING_CONTACTS,
        FINISHING
    }
    private AppDatabase database;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<String> requiredPermissionLauncher;
    private String requestedPermission = "";

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

        requiredPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (!isGranted) {
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Necessary Permission Rejected")
                        .setMessage(requestedPermission + " is a necessary permission eSMS requires in order to function properly.")
                        .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requiredPermissionLauncher.launch(requestedPermission);
                            }
                        })
                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);
                            }
                        })
                        .create()
                        .show();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission(POST_NOTIFICATIONS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission(SEND_SMS);
            requestPermission(READ_SMS);
            requestPermission(RECEIVE_SMS);
        }



        binding.btnPasscodeConfirm.setOnClickListener(v -> {
            if (!binding.txtPasscode.getText().toString().isEmpty())
                loading(LoadingStep.SETTING_PASSWORD);
            else
                Snackbar.make(binding.getRoot(), "Enter the passcode.", Snackbar.LENGTH_LONG).show();
        });
    }

    private void requestPermission(String permission) {
        requestedPermission = permission;
        requiredPermissionLauncher.launch(requestedPermission);
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
            session.setString("my_security_key", generateRandomPassword(16));
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

    public static String generateRandomPassword(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("Password length must be at least 1");
        }

        // Define the character sets
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*()-_+=[]{}\\|;:'\",.<>/?";  // Common special characters

        // Combine all characters
        String allChars = uppercase + lowercase + digits + special;

        // Convert to list for shuffling
        List<Character> characters = allChars.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());

        // Shuffle for randomness
        Collections.shuffle(characters, new SecureRandom());

        // Simple version: take the first 'length' characters after shuffling
        StringBuilder password = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        return password.toString();
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