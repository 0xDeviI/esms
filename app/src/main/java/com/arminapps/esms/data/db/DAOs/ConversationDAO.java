package com.arminapps.esms.data.db.DAOs;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.arminapps.esms.data.models.Conversation;
import com.arminapps.esms.data.models.ConversationData;
import com.arminapps.esms.data.models.NamedUnknownConversation;

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

    @Query("SELECT c.*, " +
            "(SELECT COUNT(*) FROM messages m " +
            "WHERE m.conversationId = c.id AND m.seen = 0 AND m.sent = 0) AS unseenMessages " +
            "FROM conversations c " +
            "ORDER BY c.lastMessageTime DESC")
    List<ConversationData> getAllConversations();

    @Query("SELECT conversations.id, conversations.phoneNumber, contacts.name as contactName " +
            "FROM conversations " +
            "INNER JOIN contacts ON contacts.phoneNumbers LIKE '%' || conversations.phoneNumber || '%' " +
            "WHERE conversations.name = 'Unknown'")
    List<NamedUnknownConversation> getNamedUnknownConversations();
    @Delete
    void deleteConversation(Conversation conversation);

    @Query("UPDATE conversations SET securityKey = :securityKey WHERE id = :conversationId")
    void updateSecurityKey(int conversationId, String securityKey);

    @Query("UPDATE conversations SET lastMessage = :lastMessage, lastMessageTime = :lastMessageTime WHERE id = :conversationId")
    void updateLastMessage(int conversationId, String lastMessage, long lastMessageTime);

    @Query("UPDATE conversations SET name = :name, phoneNumber = :phoneNumber WHERE id = :conversationId")
    void updateUnknownConversation(int conversationId, String name, String phoneNumber);
}
