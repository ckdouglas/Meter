package com.example.lime.meter.utils;

public class DatabaseHelper {

    public static boolean barCodeIsInDb(String barCode) {

        return true;
    }


    public static boolean userIsAvailable(String code) {
        return true;
    }

    public static boolean codeIsForCurrentUser(String code) {
        if (code.equals("1234567890")){
            return true;
        }
        return false;
    }
}
