package com.arminapps.esms.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.arminapps.esms.data.models.Contact;

import java.util.ArrayList;

public class ContactHelper {

    // Request permission in your Activity
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    public void requestContactPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            getContacts(activity);
        }
    }

    @SuppressLint("Range")
    public ArrayList<Contact> getContacts(Context context) {
        ArrayList<Contact> contactList = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();

        Cursor cursor = cr.query(
                ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null
        );

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String id = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts._ID));
                @SuppressLint("Range") String name = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Contact contact = new Contact(name);

                // Get phone numbers
                if (cursor.getInt(cursor.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor phoneCursor = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null
                    );

                    if (phoneCursor != null) {
                        while (phoneCursor.moveToNext()) {
                            String phone = phoneCursor.getString(
                                    phoneCursor.getColumnIndex(
                                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contact.addPhoneNumber(phone);
                        }
                        phoneCursor.close();
                    }
                }

                contactList.add(contact);
            }
            cursor.close();
        }

        return contactList;
    }
}
