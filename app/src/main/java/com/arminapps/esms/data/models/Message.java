package com.arminapps.esms.data.models;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages", foreignKeys = {
        @ForeignKey(
                entity = Conversation.class,
                parentColumns = "id",
                childColumns = "conversationId",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )
})
public class Message {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private boolean sent;
    private String message;
    private long time;
    private int conversationId;

    public Message() {
    }

    public Message(boolean sent, String message, long time, int conversationId) {
        this.sent = sent;
        this.message = message;
        this.time = time;
        this.conversationId = conversationId;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
