package com.arminapps.esms.data.db.DAOs;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.arminapps.esms.data.models.Conversation;

import java.util.List;

@Dao
public interface ConversationDAO {
    @Insert
    long insert(Conversation conversation);

    @Update
    void update(Conversation conversation);

    @Query("SELECT * FROM conversations WHERE phoneNumber = :phoneNumber LIMIT 1;")
    Conversation getConversationByPhoneNumber(String phoneNumber);

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1;")
    Conversation getConversationById(int id);

    @Query("SELECT * FROM conversations;")
    List<Conversation> getAllConversations();
}
