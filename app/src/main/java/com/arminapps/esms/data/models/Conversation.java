package com.arminapps.esms.data.models;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "conversations")
public class Conversation {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String phoneNumber;
    private String lastMessage;
    private long lastMessageTime;

    public Conversation(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public Message getLastMessageObject() {
        return new Message(false, lastMessage, lastMessageTime, getId());
    }

    public void setLastMessageObject(Message lastMessage) {
        this.lastMessage = lastMessage.getMessage();
        this.lastMessageTime = lastMessage.getTime();
    }
}
