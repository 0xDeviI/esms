package com.arminapps.esms.views.main.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arminapps.esms.R;
import com.arminapps.esms.adapters.ContactAdapter;
import com.arminapps.esms.data.db.AppDatabase;
import com.arminapps.esms.data.models.Contact;
import com.arminapps.esms.databinding.FragmentContactsBinding;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    private FragmentContactsBinding binding;
    private ContactAdapter adapter;
    private AppDatabase database;
    private List<Contact> contacts = new ArrayList<>();

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = AppDatabase.getInstance(getActivity());
        adapter = new ContactAdapter(getActivity(), contacts);
        binding.contactsRecycler.setLayoutManager(new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.VERTICAL,
                false
        ));
        binding.contactsRecycler.setAdapter(adapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Contact> dbContacts = database.contactDAO().getContacts();
                getActivity().runOnUiThread(() -> {
                    contacts.clear();
                    contacts.addAll(dbContacts);
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contacts, container, false);
        return binding.getRoot();
    }
}