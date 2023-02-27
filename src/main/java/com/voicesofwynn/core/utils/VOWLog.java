package com.voicesofwynn.core.utils;

/**
 * Different versions of the game have different way to handle logging
 * Replace instance with the proper logging method
 */
public class VOWLog {

    public static VOWLog instance = new VOWLog();

    public void locLog(String str) {
        System.out.println("[LOG] " + str);
    }

    public void locWarn(String str) {
        System.out.println("[WARNING] " + str);
    }

    public void locError(String str) {
        System.out.println("[ERROR] " + str);
    }



    public static void log(String str) {
        instance.locLog(str);
    }

    public static void warn(String str) {
        instance.locWarn(str);
    }

    public static void error(String str) {
        instance.locError(str);
    }


}
