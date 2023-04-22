package com.voicesofwynn.core.events;

/*
 * Purpose of this class: Decreases the amount of useless event listeners in the rest of the code.
 * What this class does : Allows you to register listeners for messages
 *
 * Mods using the VOW Core mod, must use this class to process any messages received by the player (Chat Message Processing)
 *
 * */

//TODO: @null: make the changes you discussed with Flora in a VC 22/04/23

import java.util.ArrayList;

public class ChatMessageEvent {

    private static ArrayList<messageListener> listeners = new ArrayList<>();

    public static void message(String str) {
        for (messageListener listener : listeners) {
            listener.message(str);
        }
    }

    public static void register(messageListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }

        listeners.add(listener);
    }

    public interface messageListener {
        void message(String str);
    }

}
