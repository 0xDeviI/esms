package com.arminapps.esms.data.models;

import androidx.room.Embedded;

public class ConversationData {
    @Embedded
    private Conversation conversation;
    private int unseenMessages;

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public int getUnseenMessages() {
        return unseenMessages;
    }

    public void setUnseenMessages(int unseenMessages) {
        this.unseenMessages = unseenMessages;
    }
}
