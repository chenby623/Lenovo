package com.example.lenovopusher.utils;

/**
 * Created by ChenBy on 2016/8/31.
 */
public class UserInformation {
    private static String username;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        UserInformation.username = username;
    }
}

