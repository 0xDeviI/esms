package com.arminapps.esms.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class SMSHelper {
    private static final String TAG = "SMSHelper";
    private static final String SMS_SENT_ACTION = "SMS_SENT_ACTION";
    private static final String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";

    private Context context;
    private SMSListener listener;

    public interface SMSListener {
        void onSMSSentSuccess();
        void onSMSSentFailure(int errorCode);
        void onSMSDelivered();
        void onSMSNotDelivered();
        void onError(Exception e);
    }

    public SMSHelper(Context context, SMSListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();

            // Generate unique request codes
            int sentRequestCode = (int) System.currentTimeMillis() & 0xffff;
            int deliveredRequestCode = ((int) System.currentTimeMillis() + 1) & 0xffff;

            // Create sent pending intent
            Intent sentIntent = new Intent(SMS_SENT_ACTION);
            PendingIntent sentPendingIntent;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                sentPendingIntent = PendingIntent.getBroadcast(
                        context,
                        sentRequestCode,
                        sentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );
            } else {
                sentPendingIntent = PendingIntent.getBroadcast(
                        context,
                        sentRequestCode,
                        sentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
            }

            // Create delivered pending intent
            Intent deliveredIntent = new Intent(SMS_DELIVERED_ACTION);
            PendingIntent deliveredPendingIntent;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                deliveredPendingIntent = PendingIntent.getBroadcast(
                        context,
                        deliveredRequestCode,
                        deliveredIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );
            } else {
                deliveredPendingIntent = PendingIntent.getBroadcast(
                        context,
                        deliveredRequestCode,
                        deliveredIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
            }

            // Register sent receiver
            BroadcastReceiver sentReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    context.unregisterReceiver(this);

                    if (listener != null) {
                        switch (getResultCode()) {
                            case android.app.Activity.RESULT_OK:
                                listener.onSMSSentSuccess();
                                break;
                            default:
                                listener.onSMSSentFailure(getResultCode());
                                break;
                        }
                    }
                }
            };

            // Register delivered receiver
            BroadcastReceiver deliveredReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    context.unregisterReceiver(this);

                    if (listener != null) {
                        switch (getResultCode()) {
                            case android.app.Activity.RESULT_OK:
                                listener.onSMSDelivered();
                                break;
                            case android.app.Activity.RESULT_CANCELED:
                                listener.onSMSNotDelivered();
                                break;
                        }
                    }
                }
            };

            // Register the receivers
            ContextCompat.registerReceiver(context, sentReceiver, new IntentFilter(SMS_SENT_ACTION), ContextCompat.RECEIVER_EXPORTED);
            ContextCompat.registerReceiver(context, deliveredReceiver, new IntentFilter(SMS_DELIVERED_ACTION), ContextCompat.RECEIVER_EXPORTED);

            // Send the SMS
            smsManager.sendTextMessage(phoneNumber, null, message,
                    sentPendingIntent, deliveredPendingIntent);

        } catch (Exception e) {
            Log.e(TAG, "Error sending SMS: " + e.getMessage());
            if (listener != null) {
                listener.onError(e);
            }
        }
    }

    public static String getErrorMessage(int errorCode) {
        switch (errorCode) {
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                return "Generic failure";
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                return "No service";
            case SmsManager.RESULT_ERROR_NULL_PDU:
                return "Null PDU";
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                return "Radio off";
            case SmsManager.RESULT_ERROR_LIMIT_EXCEEDED:
                return "Limit exceeded";
            case SmsManager.RESULT_ERROR_FDN_CHECK_FAILURE:
                return "FDN check failure";
            case SmsManager.RESULT_ERROR_SHORT_CODE_NOT_ALLOWED:
                return "Short code not allowed";
            case SmsManager.RESULT_ERROR_SHORT_CODE_NEVER_ALLOWED:
                return "Short code never allowed";
            case SmsManager.RESULT_RADIO_NOT_AVAILABLE:
                return "Radio not available";
            case SmsManager.RESULT_NETWORK_REJECT:
                return "Network reject";
            case SmsManager.RESULT_INVALID_ARGUMENTS:
                return "Invalid arguments";
            case SmsManager.RESULT_INVALID_STATE:
                return "Invalid state";
            case SmsManager.RESULT_NO_MEMORY:
                return "No memory";
            case SmsManager.RESULT_INVALID_SMS_FORMAT:
                return "Invalid SMS format";
            case SmsManager.RESULT_SYSTEM_ERROR:
                return "System error";
            case SmsManager.RESULT_MODEM_ERROR:
                return "Modem error";
            case SmsManager.RESULT_NETWORK_ERROR:
                return "Network error";
            case SmsManager.RESULT_INVALID_SMSC_ADDRESS:
                return "Invalid SMSC address";
            case SmsManager.RESULT_OPERATION_NOT_ALLOWED:
                return "Operation not allowed";
            case SmsManager.RESULT_INTERNAL_ERROR:
                return "Internal error";
            default:
                return "Unknown error: " + errorCode;
        }
    }
}
