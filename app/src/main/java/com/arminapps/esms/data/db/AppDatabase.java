package com.arminapps.esms.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.arminapps.esms.data.db.DAOs.ContactDAO;
import com.arminapps.esms.data.db.converters.StringListConverter;
import com.arminapps.esms.data.models.Contact;

@Database(entities = {Contact.class}, version = 1)
@TypeConverters({StringListConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract ContactDAO contactDAO();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "eSMS_db"
            ).build();
        }
        return instance;
    }
}
