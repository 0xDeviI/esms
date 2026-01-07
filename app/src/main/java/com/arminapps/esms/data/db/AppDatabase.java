package com.arminapps.esms.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.arminapps.esms.data.db.DAOs.ContactDAO;
import com.arminapps.esms.data.db.DAOs.ConversationDAO;
import com.arminapps.esms.data.db.DAOs.MessageDAO;
import com.arminapps.esms.data.db.converters.MessageConverter;
import com.arminapps.esms.data.db.converters.StringListConverter;
import com.arminapps.esms.data.models.Contact;
import com.arminapps.esms.data.models.Conversation;
import com.arminapps.esms.data.models.Message;

import net.sqlcipher.database.SupportFactory;

@Database(entities = {Contact.class, Conversation.class, Message.class}, version = 1)
@TypeConverters({StringListConverter.class, MessageConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract ContactDAO contactDAO();
    public abstract MessageDAO messageDAO();

    public abstract ConversationDAO conversationDAO();
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            byte[] passwordBytes = ";1&.YD.8C4]Ao@pm".getBytes();
            SupportFactory factory = new SupportFactory(passwordBytes);

            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "eSMS_db"
            )
                    .openHelperFactory(
                            factory
                    ).build();
        }
        return instance;
    }
}
