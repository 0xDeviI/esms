package com.arminapps.esms.data.db.converters;

import androidx.room.TypeConverter;

import com.arminapps.esms.data.models.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MessageConverter {
    @TypeConverter
    public static Message fromString(String value) {
        if (value == null || value.isEmpty()) return new Message();
        return new Gson().fromJson(value, Message.class);
    }

    @TypeConverter
    public static String fromMessage(Message message) {
        return new Gson().toJson(message);
    }

}