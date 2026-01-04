package com.arminapps.esms.adapters;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.arminapps.esms.R;
import com.arminapps.esms.data.models.Message;
import com.arminapps.esms.databinding.MessageLayoutBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

    private Context context;
    private List<Message> messages;
    private final static String DATE_FORMAT = "yyyy/MM/dd-HH:mm aa";

    public ChatMessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageLayoutBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.message_layout,
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (message.isSent()) {
            holder.binding.messageSentLayout.setVisibility(VISIBLE);
            holder.binding.txtMessageSent.setText(message.getMessage());
            holder.binding.txtSentTime.setText(
                    new SimpleDateFormat(DATE_FORMAT).format(
                            new Date(message.getTime())
                    )
            );
        }
        else {
            holder.binding.messageReceivedLayout.setVisibility(VISIBLE);
            holder.binding.txtMessageReceived.setText(message.getMessage());
            holder.binding.txtReceivedTime.setText(
                    new SimpleDateFormat(DATE_FORMAT).format(
                            new Date(message.getTime())
                    )
            );
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private MessageLayoutBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public ViewHolder(@NonNull MessageLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
