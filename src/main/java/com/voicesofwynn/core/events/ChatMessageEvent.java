package com.voicesofwynn.core.events;

import com.voicesofwynn.core.VOWCore;
import java.util.ArrayList;

/**
 * Purpose of this class: Decreases the amount of useless event listeners in the rest of the code.<p>
 * What this class does : Allows you to register listeners for messages<p>
 * Mods using the VOW Core mod, must use this class to process any messages received by the player (Chat Message Processing)<p>
 */
public class ChatMessageEvent {

    private static final ArrayList<messageListener> listeners = new ArrayList<>();

    public static void message(String str) {
        if (!VOWCore.isWorking()) { // Prevents code from running during the updating of the mod (downloading voice files, ...)
            return;
        }

        for (messageListener listener : listeners) {
            listener.message(str);
        }
    }

    public static void register(messageListener listener) { // Registers the chat register
        listeners.add(listener);
    }

    public interface messageListener {
        void message(String str);
    }

}
