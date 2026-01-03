package com.arminapps.esms.adapters;

import static android.view.View.GONE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.arminapps.esms.R;
import com.arminapps.esms.data.models.Contact;
import com.arminapps.esms.databinding.ContactLayoutBinding;

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
