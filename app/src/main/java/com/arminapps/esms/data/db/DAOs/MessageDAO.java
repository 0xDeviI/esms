package com.arminapps.esms.data.db.DAOs;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.arminapps.esms.data.models.Message;

import java.util.List;

@Dao
public interface MessageDAO {
    @Insert
    long insert(Message message);

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId;")
    List<Message> getMessagesByConversationId(int conversationId);
}
