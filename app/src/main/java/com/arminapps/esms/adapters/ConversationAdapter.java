package com.arminapps.esms.adapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.arminapps.esms.R;
import com.arminapps.esms.data.models.Conversation;
import com.arminapps.esms.data.models.ConversationData;
import com.arminapps.esms.data.models.Message;
import com.arminapps.esms.databinding.ContactLayoutBinding;
import com.arminapps.esms.databinding.ConversationLayoutBinding;
import com.arminapps.esms.views.chat.ChatActivity;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    private Context context;
    private List<ConversationData> conversations;
    private final static String DATE_FORMAT = "yyyy/MM/dd\nHH:mm aa";


    public ConversationAdapter(Context context, List<ConversationData> conversations) {
        this.context = context;
        this.conversations = conversations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConversationLayoutBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.conversation_layout,
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConversationData conversationData = conversations.get(position);
        Conversation conversation = conversationData.getConversation();

        String contactName = conversation.getName();
        holder.binding.avatarText.setText(String.valueOf(contactName.charAt(0)));
        holder.binding.txtContactName.setText(contactName);
        String lastMessage = conversation.getLastMessage();
        if (lastMessage.isEmpty())
            holder.binding.txtMessage.setVisibility(GONE);
        else {
            holder.binding.txtDate.setVisibility(VISIBLE);
            holder.binding.txtMessage.setVisibility(VISIBLE);
            holder.binding.txtMessage.setText(lastMessage);
            holder.binding.txtDate.setVisibility(VISIBLE);
            holder.binding.txtDate.setText(new SimpleDateFormat(DATE_FORMAT)
                    .format(conversation.getLastMessageTime()));
            if (conversationData.getUnseenMessages() > 0) {
                holder.binding.txtUnseenMessages.setText(String.valueOf(
                        conversationData.getUnseenMessages()
                ));
                holder.binding.txtUnseenMessages.setVisibility(VISIBLE);
            }
        }

        holder.binding.contactCard.setOnClickListener(v -> {
            context.startActivity(
                    new Intent(
                            context,
                            ChatActivity.class
                    ).putExtra("conversation_loading", true)
                            .putExtra("conversation_id", conversation.getId())
            );
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ConversationLayoutBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public ViewHolder(@NonNull ConversationLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
