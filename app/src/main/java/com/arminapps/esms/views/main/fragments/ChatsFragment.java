package com.arminapps.esms.views.main.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arminapps.esms.R;
import com.arminapps.esms.adapters.ConversationAdapter;
import com.arminapps.esms.data.db.AppDatabase;
import com.arminapps.esms.data.models.Contact;
import com.arminapps.esms.data.models.Conversation;
import com.arminapps.esms.data.models.ConversationData;
import com.arminapps.esms.data.models.NamedUnknownConversation;
import com.arminapps.esms.databinding.FragmentChatsBinding;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;
    private AppDatabase database;
    private List<ConversationData> conversations = new ArrayList<>();
    private ConversationAdapter adapter;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = AppDatabase.getInstance(getActivity());
        loadChats();
    }


    private void loadChats() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NamedUnknownConversation> unknownConversations = database.conversationDAO().getNamedUnknownConversations();
                for (NamedUnknownConversation namedUnknownConversation :
                        unknownConversations) {
                    database.conversationDAO().updateUnknownConversation(
                            namedUnknownConversation.getId(),
                            namedUnknownConversation.getContactName(),
                            namedUnknownConversation.getPhoneNumber()
                    );
                }

                if (adapter == null)
                    conversations = database.conversationDAO().getAllConversations();
                else {
                    conversations.clear();
                    conversations.addAll(database.conversationDAO().getAllConversations());
                }

                getActivity().runOnUiThread(() -> {
                    if (adapter == null)
                        adapter = new ConversationAdapter(getActivity(), conversations);
                    else
                        adapter.notifyDataSetChanged();

                    if (conversations.isEmpty()) {
                        binding.viewNoConversation.setVisibility(VISIBLE);
                        binding.conversationsRecyclerView.setVisibility(GONE);
                    }
                    else {
                        binding.conversationsRecyclerView.setLayoutManager(new LinearLayoutManager(
                                getActivity(),
                                LinearLayoutManager.VERTICAL,
                                false
                        ));
                        if (binding.conversationsRecyclerView.getAdapter() == null)
                            binding.conversationsRecyclerView.setAdapter(adapter);

                        binding.conversationsRecyclerView.setVisibility(VISIBLE);
                        binding.viewNoConversation.setVisibility(GONE);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChats();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false);
        return binding.getRoot();
    }
}