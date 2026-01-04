package com.arminapps.esms.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "contacts")
public class Contact {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private ArrayList<String> phoneNumbers;

    public Contact(String name) {
        this.name = name;
        this.phoneNumbers = new ArrayList<>();
    }

    // Getters and setters
    public void addPhoneNumber(String phoneNumber) {
        phoneNumbers.add(getCleanPhoneNumber(phoneNumber));
    }

    private String getCleanPhoneNumber(String phoneNumber) {
        return phoneNumber.replace(" ", "")
                .replace("-", "");
    }

    public void addPhoneNumbers(List<String> phoneNumbers) {
        phoneNumbers.forEach(phoneNumber -> {
            this.phoneNumbers.add(getCleanPhoneNumber(phoneNumber));
        });
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumbers(ArrayList<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}
