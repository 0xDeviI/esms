package com.arminapps.esms.views.addContact;

import static android.Manifest.permission.READ_CONTACTS;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.arminapps.esms.R;
import com.arminapps.esms.adapters.PhoneNumberAdditionAdapter;
import com.arminapps.esms.data.db.AppDatabase;
import com.arminapps.esms.data.models.Contact;
import com.arminapps.esms.databinding.ActivityAddContactBinding;
import com.arminapps.esms.utils.SessionManager;
import com.arminapps.esms.views.contacts.ContactsActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class AddContactActivity extends AppCompatActivity {

    private ActivityAddContactBinding binding;
    private List<String> phoneNumbers = new ArrayList<>();
    private PhoneNumberAdditionAdapter adapter;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_contact);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        binding.toolbar.setNavigationOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        setup();
    }

    private void setup() {
        phoneNumbers.add("");
        adapter = new PhoneNumberAdditionAdapter(this, phoneNumbers);
        binding.phoneNumbersRecycler.setAdapter(adapter);
        binding.phoneNumbersRecycler.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
        ));
        binding.btnNewPhoneNumber.setOnClickListener(v -> {
            phoneNumbers.add("");
            adapter.notifyItemInserted(phoneNumbers.size() - 1);
        });

        binding.txtContactName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!binding.txtContactName.getText().toString().isEmpty())
                    binding.txtAvatar.setText(String.valueOf(binding.txtContactName.getText().toString().charAt(0)));
                else
                    binding.txtAvatar.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_contact_menu, menu);
        return true;
    }

    private boolean canSave() {
        if (binding.txtContactName.getText().toString().isEmpty()) {
            Snackbar.make(binding.btnNewPhoneNumber, "Enter contact name.", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        boolean validPhones = true;
        for (int i = 0; i < phoneNumbers.size(); i++) {
            String phoneNumber = phoneNumbers.get(i).trim();
            if (phoneNumber.isEmpty()) {
                validPhones = false;
                Snackbar.make(binding.getRoot(), "Phone numbers can't be empty.", Snackbar.LENGTH_SHORT).show();
                break;
            }
            if (!PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
                Snackbar.make(binding.getRoot(), "'" + phoneNumber + "' is invalid phone number.", Snackbar.LENGTH_SHORT).show();
                validPhones = false;
                break;
            }
        }

        return validPhones;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save_contact) {
            if (canSave()) {
                database = AppDatabase.getInstance(getApplicationContext());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // save in DB
                        Contact contact = new Contact(binding.txtContactName.getText().toString());
                        contact.addPhoneNumbers(phoneNumbers);
                        database.contactDAO().insert(contact);
                        startActivity(new Intent(AddContactActivity.this, ContactsActivity.class)
                                .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }
                }).start();
            }
            return true;
        }
        else if (id == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}