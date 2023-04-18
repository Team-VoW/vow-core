package com.voicesofwynn.core.registers;

import com.voicesofwynn.core.events.ChatMessageEvent;
import com.voicesofwynn.core.wrappers.FilePathProvider;
import com.voicesofwynn.core.wrappers.VOWLocationProvider;

import java.util.HashMap;
import java.util.Map;

public class DialogueRegister implements ChatMessageEvent.messageListener {

    public static class Dialog {
        public String line;
        public VOWLocationProvider location;

        public FilePathProvider file;
    }

    public static DialogueRegister instance;

    private final Map<String, Dialog> dialogs;

    public DialogueRegister() {
        instance = this;
        dialogs = new HashMap<>();
        ChatMessageEvent.register(this);
    }

    public static DialogueRegister getInstance() {
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
        // TODO: Make work
    }
}
