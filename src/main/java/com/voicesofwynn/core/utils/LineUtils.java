package com.voicesofwynn.core.utils;

public class LineUtils {

    public static String lineFromMessage (String message) {
        message = message.trim();
        message = message.toLowerCase();
        message = message.replaceAll("[^abcdefghijklmnopqrstuvwxyz?.!0123456789/]", "");
        return message;
    }

    public static String npcNameFromLine(String line) {
        int startingIndex = line.indexOf("]");
        int endingIndex = line.indexOf(":");

        if (startingIndex == -1 || endingIndex == -1) {
            return null;
        }

        return line.substring(
                line.indexOf("]"),
                line.indexOf(":")
        ).trim().toLowerCase().replaceAll("[^a-z\\d?]", "");
    }

}
