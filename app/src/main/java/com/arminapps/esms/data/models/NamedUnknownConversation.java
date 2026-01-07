package com.arminapps.esms.data.models;

public class NamedUnknownConversation {
    private int id;
    private String phoneNumber;
    private String contactName;

    public NamedUnknownConversation(int id, String phoneNumber, String contactName) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.contactName = contactName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}
