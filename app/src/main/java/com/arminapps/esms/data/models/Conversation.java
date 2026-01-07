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
    private String securityKey = "";
    @Ignore
    private int unseenMessages = 0;

    @Ignore
    public Conversation(String name, String phoneNumber, String lastMessage, long lastMessageTime) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public Conversation(String name, String phoneNumber, String lastMessage, long lastMessageTime, String securityKey) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.securityKey = securityKey;
    }

    @Ignore
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

    public String getLastMessage() {
        return lastMessage;
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

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public int getUnseenMessages() {
        return unseenMessages;
    }

    public void setUnseenMessages(int unseenMessages) {
        this.unseenMessages = unseenMessages;
    }
}
