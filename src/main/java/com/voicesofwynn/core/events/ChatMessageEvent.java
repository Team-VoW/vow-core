package com.voicesofwynn.core.events;

import java.util.List;

public class ChatMessageEvent {

    private static List<messageListener> listeners;

    public static void message(String str) {
        for (messageListener listener : listeners) {
            listener.message(str);
        }
    }

    public interface messageListener {
        void message(String str);
    }

}