package com.arminapps.esms.data.db.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class StringListConverter {

    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        if (value == null || value.isEmpty()) return new ArrayList<>();
        return new Gson().fromJson(value, new TypeToken<List<String>>() {}.getType());
    }

    @TypeConverter
    public static String fromList(ArrayList<String> list) {
        return new Gson().toJson(list);
    }
}
