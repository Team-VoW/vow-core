package com.voicesofwynn.core.events;

/*
 * Purpose of this class: Decreases the amount of useless event listeners in the rest of the code.
 * What this class does : Allows you to register listeners for messages
 *
 * Mods using the VOW Core mod, must use this class to process any messages received by the player (Chat Message Processing)
 *
 * */

import com.voicesofwynn.core.VOWCore;

import java.util.ArrayList;

public class ChatMessageEvent {

    private static final ArrayList<messageListener> listeners = new ArrayList<>();

    public static void message(String str) {
        if (!VOWCore.isWorking()) {
            return;
        }

        for (messageListener listener : listeners) {
            listener.message(str);
        }
    }

    public static void register(messageListener listener) {
        listeners.add(listener);
    }

    public interface messageListener {
        void message(String str);
    }

}
