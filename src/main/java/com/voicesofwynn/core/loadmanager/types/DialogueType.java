package com.voicesofwynn.core.loadmanager.types;

import com.voicesofwynn.core.loadmanager.RegisterType;
import com.voicesofwynn.core.loadmanager.WriteInstanceValues;
import com.voicesofwynn.core.registers.DialogueRegister;
import com.voicesofwynn.core.utils.ByteUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class DialogueType implements RegisterType {
    @Override
    public void load(FileInputStream reader) throws IOException {
        int amountOfDialogs = ByteUtils.readInteger(reader);

        DialogueRegister register = DialogueRegister.getInstance();
        if (register == null) {
            register = new DialogueRegister();
        }

        for (int i = 0 ; i < amountOfDialogs ; i++) {
            String text = ByteUtils.readString(reader);
            DialogueRegister.Dialog dialog = new DialogueRegister.Dialog();
            dialog.line = text;

            register.register(dialog);
        }

    }

    @Override
    public void write(FileOutputStream writer, Object section, WriteInstanceValues value) throws IOException {
        if (section instanceof Map) {
            Map<?, ?> sectionMap = (Map<?, ?>) section;

            writer.write(ByteUtils.encodeInteger(sectionMap.size()));
            for (Map.Entry<?, ?> dialog : sectionMap.entrySet()) {
                if (!(dialog.getKey() instanceof String)) {
                    throw new RuntimeException("In file " + value.file + " the dialogs are messed up?! What?");
                }
                String key = (String) dialog.getKey();

                writer.write(ByteUtils.encodeString(key));



            }
        } else {
            throw new RuntimeException("In file " + value.file + " the dialog(s) don't have dialogs and instead have a " + section.getClass().getName());
        }
    }

    @Override
    public boolean isWriteTimeOnly() {
        return false;
    }

    @Override
    public String getName() {
        return "dialogue";
    }
}
