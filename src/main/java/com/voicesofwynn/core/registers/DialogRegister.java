package com.voicesofwynn.core.registers;

import com.voicesofwynn.core.events.ChatMessageEvent;
import com.voicesofwynn.core.wrappers.VOWLocationProvider;

import java.util.HashMap;
import java.util.Map;

public class DialogRegister implements ChatMessageEvent.messageListener {

    public static class Dialog {
        String line;
        VOWLocationProvider location;
    }

    public static DialogRegister instance;

    private final Map<String, Dialog> dialogs;

    public DialogRegister () {
        instance = this;
        dialogs = new HashMap<>();
        ChatMessageEvent.register(this);
    }

    public static DialogRegister getInstance() {
        return instance;
    }

    public void register(Dialog dialog) {
        dialogs.put(dialog.line, dialog);
    }

    // mostly a debug method but may have a use later
    public Map<String, Dialog> getDialogs() {
        return dialogs;
    }

    @Override
    public void message(String str) {

    }
}
