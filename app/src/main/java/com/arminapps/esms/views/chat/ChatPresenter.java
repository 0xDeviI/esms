package com.arminapps.esms.views.chat;

import com.arminapps.esms.data.db.AppDatabase;
import com.arminapps.esms.data.models.Conversation;
import com.arminapps.esms.data.models.Message;
import com.arminapps.esms.utils.SMSHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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

        SMSHelper smsHelper = new SMSHelper(activity, new SMSHelper.SMSListener() {
            @Override
            public void onSMSSentSuccess() {
                showErrorDialog("SENT");
            }

            @Override
            public void onSMSSentFailure(int errorCode) {
                showErrorDialog(SMSHelper.getErrorMessage(errorCode));
            }

            @Override
            public void onSMSDelivered() {
                registerESMS(conversation, message);
            }

            @Override
            public void onSMSNotDelivered() {
                showErrorDialog("DIDN'T DELIVER");
            }

            @Override
            public void onError(Exception e) {
                showErrorDialog(e.getMessage());
            }
        });

        smsHelper.sendSMS(conversation.getPhoneNumber(), message.getMessage());
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
                    Conversation createdConversation = new Conversation(conversation.getName(), conversation.getPhoneNumber());
                    long conversationId = database.conversationDAO().insert(createdConversation);
                    message.setConversationId((int) conversationId);
                    long messageId = database.messageDAO().insert(message);
                    message.setId((int) messageId);

                    createdConversation.setLastMessageObject(message);
                    database.conversationDAO().update(createdConversation);
                }
            }).start();
        }
        else {
            database.messageDAO().insert(message);
            conversation.setLastMessageObject(message);
            database.conversationDAO().update(conversation);
        }
        view.messageSent(message);
    }
}
