package com.arminapps.esms.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.arminapps.esms.R;
import com.arminapps.esms.databinding.AddingPhoneNumberViewBinding;

import java.util.List;

public class PhoneNumberAdditionAdapter extends RecyclerView.Adapter<PhoneNumberAdditionAdapter.ViewHolder> {

    private Context context;
    private List<String> phoneNumbers;

    public PhoneNumberAdditionAdapter(Context context, List<String> phoneNumbers) {
        this.context = context;
        this.phoneNumbers = phoneNumbers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AddingPhoneNumberViewBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.adding_phone_number_view,
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return phoneNumbers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private AddingPhoneNumberViewBinding binding;
        private TextWatcher textWatcher;

        public ViewHolder(@NonNull AddingPhoneNumberViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int position) {
            // Remove existing TextWatcher if any
            if (textWatcher != null) {
                binding.txtPhoneNumber.removeTextChangedListener(textWatcher);
            }

            // Set current text
            String phoneNumber = phoneNumbers.get(position);
            binding.txtPhoneNumber.setText(phoneNumber != null ? phoneNumber : "");

            // Set delete icon listener
            binding.phoneNumberContainer.setEndIconOnClickListener(v -> {
                int currentPosition = getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    phoneNumbers.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                }
            });

            // Create and set new TextWatcher
            textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    int currentPosition = getAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION && currentPosition < phoneNumbers.size()) {
                        phoneNumbers.set(currentPosition, s.toString());
                    }
                }
            };

            binding.txtPhoneNumber.addTextChangedListener(textWatcher);

            // Optional: Remove focus listener as TextWatcher already handles updates
            // Or keep it for final validation on focus loss
            binding.txtPhoneNumber.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    int currentPosition = getAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION && currentPosition < phoneNumbers.size()) {
                        String newPhoneNumber = binding.txtPhoneNumber.getText().toString();
                        phoneNumbers.set(currentPosition, newPhoneNumber);
                    }
                }
            });
        }
    }
}