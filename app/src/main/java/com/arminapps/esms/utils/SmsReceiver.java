package com.arminapps.esms.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.arminapps.esms.data.db.AppDatabase;
import com.arminapps.esms.data.models.Conversation;
import com.arminapps.esms.data.models.Message;

import org.greenrobot.eventbus.EventBus;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SmsReceiver extends BroadcastReceiver {

    private AppDatabase database;
    private Conversation conversation;
    private SessionManager session;
    private String securityKey = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            database = AppDatabase.getInstance(context);
            session = new SessionManager(context);
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                String format = bundle.getString("format");

                if (pdus != null) {
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    StringBuilder fullMessage = new StringBuilder();

                    for (int i = 0; i < pdus.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                        fullMessage.append(messages[i].getMessageBody());
                    }

                    String sender = PhoneNumberUtils.normalizeToE164(
                            context,
                            messages[0].getOriginatingAddress()
                    ); // Sender's phone number
                    String messageBody = fullMessage.toString();

                    String unstampped = unstamp(messageBody);
                    if (unstampped != null && unstampped.startsWith(Crypto.get_())) {
                        messageBody = unstampped.substring(Crypto.get_().length());
                        boolean isDecrypted = true;
                        String decryptedMessage = "";
                        try {
                            securityKey = session.getString("my_security_key");
                            decryptedMessage = Crypto.decrypt(messageBody, securityKey);
                        }
                        catch (Exception ex) {
                            isDecrypted = false;
                        }

                        // Finding conversation
                        boolean finalIsDecrypted = isDecrypted;
                        String finalDecryptedMessage = decryptedMessage;
                        String finalMessageBody = messageBody;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                conversation = database.conversationDAO().getConversationByPhoneNumber(sender);
                                if (conversation == null) {
                                    conversation = new Conversation("Unknown", sender);
                                    long conversationId = database.conversationDAO().insert(conversation);
                                    conversation.setId((int) conversationId);
                                }

                                Message message;
                                if (finalIsDecrypted)
                                    message = new Message(false, finalDecryptedMessage, new Date().getTime(), conversation.getId(), true, false, false);
                                else
                                    message = new Message(false, finalMessageBody, new Date().getTime(), conversation.getId(), true, true, false);
                                long messageId = database.messageDAO().insert(message);
                                message.setId((int) messageId);
                                if (finalIsDecrypted)
                                    conversation.setLastMessage(finalDecryptedMessage);
                                else
                                    conversation.setLastMessage("* ENCRYPTED *");
                                conversation.setLastMessageTime(message.getTime());
                                database.conversationDAO().updateLastMessage(
                                        conversation.getId(),
                                        message.getMessage(),
                                        message.getTime()
                                );

                                EventBus.getDefault().post(message);

                                // Notify user
                                Notification.sendNotification(context, sender, "Tap to open the encrypted message.", conversation.getId());
                            }
                        }).start();
                        abortBroadcast();
                    }
                }
            }
        }
    }

    private String unstamp(String str) {
        try {
            byte[] message = Crypto.xor(Base64.getDecoder().decode(str), Crypto.get_());
            return new String(message, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
