package com.arminapps.esms.views.chat;

import android.telephony.SmsManager;

import com.arminapps.esms.data.db.AppDatabase;
import com.arminapps.esms.data.models.Conversation;
import com.arminapps.esms.data.models.Message;
import com.arminapps.esms.utils.Crypto;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class ChatPresenter implements ChatContract.Presenter {
    private ChatActivity activity;
    private ChatContract.View view;
    private AppDatabase database;

    public ChatPresenter(ChatActivity chatActivity) {
        this.activity = chatActivity;
        this.view = chatActivity;
        database = AppDatabase.getInstance(chatActivity);
    }

    @Override
    public void loadMessages(Conversation conversation) {
        if (conversation.getId() == -1) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                view.setMessages(
                        database.messageDAO().getMessagesByConversationId(conversation.getId())
                );
            }
        }).start();
    }

    @Override
    public void sendSMS(Conversation conversation, Message message) {
        try {
            message.setEncrypted(!conversation.getSecurityKey().isEmpty());

            SmsManager smsManager = SmsManager.getDefault();
            String finalMessage = message.getMessage();
            if (message.isEncrypted()) {
                finalMessage = Crypto.stampMessage(
                        Crypto.encrypt(message.getMessage(), conversation.getSecurityKey())
                );
            }

            if (finalMessage.length() > 160) {
                ArrayList<String> parts = smsManager.divideMessage(finalMessage);
                smsManager.sendMultipartTextMessage(conversation.getPhoneNumber(), null, parts,
                        null, null);
            }
            else {
                smsManager.sendTextMessage(conversation.getPhoneNumber(), null, finalMessage, null,null);
            }

            registerESMS(conversation, message);
        } catch (Exception e) {
            showErrorDialog(e.getMessage());
        }
    }

    @Override
    public void showErrorDialog(String errorMessage) {
        new MaterialAlertDialogBuilder(activity)
                .setTitle("Sending failed")
                .setMessage(errorMessage)
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    @Override
    public void checkConversationExist(Conversation conversation) {
        if (conversation.getId() == -1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Conversation dbConversation = database.conversationDAO().getConversationByPhoneNumber(conversation.getPhoneNumber());
                    if (dbConversation == null)
                        view.loadConversation(conversation);
                    else
                        view.loadConversation(dbConversation);
                }
            }).start();
        }
        else
            new Thread(new Runnable() {
                @Override
                public void run() {
                    view.loadConversation(database.conversationDAO().getConversationById(conversation.getId()));
                }
            }).start();
    }

    @Override
    public void registerESMS(Conversation conversation, Message message) {
        if (conversation.getId() == -1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Conversation createdConversation = new Conversation(
                            conversation.getName(), conversation.getPhoneNumber(),
                            message.getMessage(), message.getTime()
                    );
                    long conversationId = database.conversationDAO().insert(createdConversation);
                    message.setConversationId((int) conversationId);
                    long messageId = database.messageDAO().insert(message);
                    message.setId((int) messageId);
                }
            }).start();
        }
        else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    database.messageDAO().insert(message);
                    conversation.setLastMessage(message.getMessage());
                    conversation.setLastMessageTime(message.getTime());
                    database.conversationDAO().update(conversation);
                }
            }).start();
        }
        view.messageSent(message);
    }

    @Override
    public void removeConversation(Conversation conversation) {
        if (conversation == null || conversation.getId() == -1) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                database.conversationDAO().deleteConversation(conversation);
                activity.runOnUiThread(() -> view.conversationRemoved());
            }
        }).start();
    }

    @Override
    public void setSecurityKey(Conversation conversation, String securityKey) {
        if (conversation == null) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                database.conversationDAO().updateSecurityKey(conversation.getId(), conversation.getSecurityKey());
                activity.runOnUiThread(() -> view.securityKeySet());
            }
        });
    }
}
