package com.example.lime.meter.utils;

public class LoginHelper {

     private static boolean isLoggedIn = true;

    public static boolean userIsLoggedIn() {
        if (isLoggedIn)
            return true;
        return false;
    }



}
