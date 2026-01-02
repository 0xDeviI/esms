package com.arminapps.esms.views.main.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arminapps.esms.R;
import com.arminapps.esms.data.db.AppDatabase;
import com.arminapps.esms.data.models.Contact;
import com.arminapps.esms.databinding.FragmentChatsBinding;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;
    private AppDatabase database;

    public ChatsFragment() {
        // Required empty public constructor
    }
    int x = 0;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false);
        return binding.getRoot();
    }
}