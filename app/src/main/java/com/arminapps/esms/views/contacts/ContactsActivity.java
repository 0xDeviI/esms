package com.arminapps.esms.views.contacts;

import static android.Manifest.permission.READ_CONTACTS;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.arminapps.esms.R;
import com.arminapps.esms.adapters.ContactAdapter;
import com.arminapps.esms.data.db.AppDatabase;
import com.arminapps.esms.data.db.DAOs.ContactDAO;
import com.arminapps.esms.data.models.Contact;
import com.arminapps.esms.databinding.ActivityContactsBinding;
import com.arminapps.esms.utils.ContactHelper;
import com.arminapps.esms.utils.SessionManager;
import com.arminapps.esms.views.addContact.AddContactActivity;
import com.arminapps.esms.views.setup.SetupActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    private ActivityContactsBinding binding;
    private ContactAdapter adapter;
    private AppDatabase database;
    private List<Contact> contacts = new ArrayList<>();
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private SessionManager session;
    private ContactsActivity contactsActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contacts);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        contactsActivity = this;

        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        binding.toolbar.setNavigationOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted)
                importContacts();
            else
                Snackbar.make(binding.getRoot(), "Contacts auto-import rejected.", Snackbar.LENGTH_LONG).show();
        });

        session = new SessionManager(this);

        loadContacts();
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
        startActivity(new Intent(
                ContactsActivity.this, ContactsActivity.class
        ).setFlags(FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void loadContacts() {
        database = AppDatabase.getInstance(getApplicationContext());
        adapter = new ContactAdapter(getApplicationContext(), contacts);
        binding.contactsRecycler.setLayoutManager(new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.VERTICAL,
                false
        ));
        binding.contactsRecycler.setAdapter(adapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Contact> dbContacts = database.contactDAO().getContacts();
                runOnUiThread(() -> {
                    if (!dbContacts.isEmpty()) {
                        binding.txtContactState.setVisibility(GONE);
                        binding.contactsRecycler.setVisibility(VISIBLE);
                        contacts.clear();
                        contacts.addAll(dbContacts);
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        binding.txtContactState.setText("No contact found.");
                        if (!session.getBoolean("contacts_imported")) {
                            new MaterialAlertDialogBuilder(contactsActivity)
                                    .setTitle("Importing Contacts")
                                    .setMessage("You didn't import contacts. You can click top-right menu to import them.")
                                    .setPositiveButton("OK", null)
                                    .create().show();
                        }
                    }
                });
            }
        }).start();

        binding.txtContactSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(binding.txtContactSearch.getText().toString().trim());
            }
        });
    }

    private void search(String searchQuery) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Contact> filteredContacts;

                if (searchQuery.isEmpty()) {
                    filteredContacts = database.contactDAO().getContacts();
                } else {
                    String searchPattern = "%" + searchQuery + "%";
                    filteredContacts = database.contactDAO().searchContact(searchPattern);
                }

                runOnUiThread(() -> {
                    if (!filteredContacts.isEmpty()) {
                        binding.txtContactState.setVisibility(GONE);
                        binding.contactsRecycler.setVisibility(VISIBLE);
                        contacts.clear();
                        contacts.addAll(filteredContacts);
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        binding.contactsRecycler.setVisibility(GONE);
                        binding.txtContactState.setVisibility(VISIBLE);
                        binding.txtContactState.setText("No contact found.");
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contacts_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_import_contacts) {
            requestPermissionLauncher.launch(READ_CONTACTS);
            return true;
        }
        else if (id == R.id.action_add_contact) {
            startActivity(new Intent(ContactsActivity.this, AddContactActivity.class));
        }
        else if (id == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}