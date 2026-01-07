package com.arminapps.esms.views.chat;

import com.arminapps.esms.data.models.Conversation;
import com.arminapps.esms.data.models.Message;

import java.util.List;

public class ChatContract {
    public interface View {
        void setup();
        void sendMessageBtnClickAction();
        void setMessages(List<Message> messages);
        void loadConversation(Conversation conversation);
        void messageSent(Message message);
        void conversationRemoved();
        void showSecurityKeyChangingDialog(String key);
        void securityKeySet();
    }

    public interface Presenter {
        void showErrorDialog(String errorMessage);
        void loadMessages(Conversation conversation);
        void sendSMS(Conversation conversation, Message message);
        void checkConversationExist(Conversation conversation);
        void registerESMS(Conversation conversation, Message message);
        void removeConversation(Conversation conversation);
        void setSecurityKey(Conversation conversation, String securityKey);
    }
}
