package com.arminapps.esms.adapters;

import static android.view.View.GONE;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.arminapps.esms.R;
import com.arminapps.esms.data.models.Contact;
import com.arminapps.esms.databinding.ContactLayoutBinding;
import com.arminapps.esms.views.chat.ChatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private Context context;
    private List<Contact> contacts;

    public ContactAdapter(Context context, List<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContactLayoutBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.contact_layout,
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);

        String contactName = contact.getName();
        holder.binding.avatarText.setText(String.valueOf(contactName.charAt(0)));
        holder.binding.txtContactName.setText(contactName);

        holder.binding.contactCard.setOnClickListener(v -> {
            List<String> phoneNumbers = contact.getPhoneNumbers();
            int phoneNumbersCount = phoneNumbers.size();
            if (phoneNumbersCount == 1)
                startChatting(contactName, phoneNumbers.get(0));
            else if (phoneNumbersCount > 1) {
                String[] adapter = phoneNumbers.toArray(new String[0]);
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Choose phone number")
                        .setItems(adapter, (dialog, which) -> {
                            startChatting(contactName, phoneNumbers.get(which));
                        })
                        .create()
                        .show();
            }
        });
    }

    private void startChatting(String name, String phoneNumber) {
        ActivityCompat.startActivity(
                context,
                new Intent(context, ChatActivity.class)
                        .putExtra("from_contacts", true)
                        .putExtra("phoneNumber", phoneNumber)
                        .putExtra("name", name),
                null
        );
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ContactLayoutBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public ViewHolder(@NonNull ContactLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
