package com.arminapps.esms.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneNumberUtils {
    private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    public static String normalizeToE164(Context context, String rawNumber) {
        if (rawNumber == null || rawNumber.isEmpty()) {
            return null;
        }

        // Get default region (e.g., "IR" for Iran)
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getNetworkCountryIso();  // e.g., "ir"
        if (countryCode == null || countryCode.isEmpty()) {
            countryCode = "IR";  // Fallback; adjust if your app targets specific countries
        }
        countryCode = countryCode.toUpperCase();

        try {
            Phonenumber.PhoneNumber number = phoneUtil.parse(rawNumber, countryCode);
            if (phoneUtil.isValidNumber(number)) {
                return phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
            }
        } catch (NumberParseException e) {
            // Log if needed
        }
        return null;  // Or fallback to stripped version if you want to keep invalid ones
    }
    public static boolean areSamePhoneNumbers(String phone1, String phone2) {
        try {
            String normalized1 = phone1.replaceAll("[^0-9]", "");
            String normalized2 = phone2.replaceAll("[^0-9]", "");

            long num1 = Long.parseLong(normalized1);
            long num2 = Long.parseLong(normalized2);

            return num1 == num2;
        } catch (NumberFormatException e) {
            String normalized1 = phone1.replaceAll("[^0-9]", "");
            String normalized2 = phone2.replaceAll("[^0-9]", "");
            return normalized1.equals(normalized2);
        }
    }
}
