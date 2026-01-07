package com.arminapps.esms.data.models;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
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
    private boolean encrypted;
    private boolean masked;
    private boolean seen = false;

    @Ignore
    public Message() {
    }

    @Ignore
    public Message(boolean sent, String message, long time, int conversationId) {
        this.sent = sent;
        this.message = message;
        this.time = time;
        this.conversationId = conversationId;
    }

    @Ignore
    public Message(boolean sent, String message, long time, int conversationId, boolean encrypted) {
        this.sent = sent;
        this.message = message;
        this.time = time;
        this.conversationId = conversationId;
        this.encrypted = encrypted;
    }

    public Message(boolean sent, String message, long time, int conversationId, boolean encrypted, boolean masked, boolean seen) {
        this.sent = sent;
        this.message = message;
        this.time = time;
        this.conversationId = conversationId;
        this.encrypted = encrypted;
        this.masked = masked;
        this.seen = seen;
    }


    public boolean isMasked() {
        return masked;
    }

    public void setMasked(boolean masked) {
        this.masked = masked;
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

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
