package com.arminapps.esms.utils;

public class PhoneNumberUtils {
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
