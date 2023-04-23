package com.voicesofwynn.core.loadmanager.types;

import com.voicesofwynn.core.loadmanager.RegisterType;
import com.voicesofwynn.core.loadmanager.WriteInstanceValues;
import com.voicesofwynn.core.loadmanager.subtypes.FileLocationSubType;
import com.voicesofwynn.core.loadmanager.subtypes.LocationSubType;
import com.voicesofwynn.core.registers.DialogueRegister;
import com.voicesofwynn.core.utils.ByteUtils;
import com.voicesofwynn.core.utils.LineUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DialogueType implements RegisterType {


    @Override
    public void prepare() {
        if (DialogueRegister.getInstance() != null) {
            DialogueRegister.getInstance().getDialogs().clear();
        }
    }

    @Override
    public void load(FileInputStream in) throws IOException {
        int amountOfDialogs = ByteUtils.readInteger(in);

        DialogueRegister register = DialogueRegister.getInstance();
        if (register == null) {
            register = new DialogueRegister();
        }

        for (int i = 0 ; i < amountOfDialogs ; i++) {
            String text = ByteUtils.readString(in);
            DialogueRegister.Dialog dialog = new DialogueRegister.Dialog();
            dialog.file = FileLocationSubType.readFileLocation(in);
            dialog.line = text;
            dialog.location = LocationSubType.readLocation(in);
            byte enabled = ByteUtils.readByte(in);
            if ((enabled & 0b0000001) == 0b0000001) {
                dialog.fallOff = ByteUtils.readFloat(in);
            }

            register.register(dialog);
        }

    }

    @Override
    public void write(FileOutputStream out, Object section, WriteInstanceValues value) throws IOException {
        if (section instanceof Map) {
            Map<?, ?> sectionMap = (Map<?, ?>) section;

            out.write(ByteUtils.encodeInteger(sectionMap.size()));
            for (Map.Entry<?, ?> dialog : sectionMap.entrySet()) {
                if (!(dialog.getKey() instanceof String)) {
                    throw new RuntimeException("In file " + value.file + " the dialogues are messed up?! What?");
                }

                String message = LineUtils.lineFromMessage((String)dialog.getKey());

                out.write(ByteUtils.encodeString(message));

                if (dialog.getValue() instanceof String) {
                    FileLocationSubType.writeFileLocation(out, dialog.getValue(), value, (String) dialog.getKey());
                    String npcName = LineUtils.npcNameFromLine((String)dialog.getKey());
                    LocationSubType.writeNpcNameLocation(out, npcName);
                    out.write(0);
                } else if (dialog.getValue() instanceof Map) {
                    Set<String> used = new HashSet<>();
                    Map<?, ?> parameters = (Map<?, ?>)dialog.getValue();

                    FileLocationSubType.writeFileLocation(out, parameters.get("file"), value, (String) dialog.getKey());
                    used.add("file");

                    Object location = parameters.get("location");
                    if (location == null) {
                        String npcName = LineUtils.npcNameFromLine((String)dialog.getKey());
                        LocationSubType.writeNpcNameLocation(out, npcName);
                    } else {
                        LocationSubType.writeLocation(out, location, "file " + value.filePath + " dialogue " + dialog.getKey());
                    }
                    used.add("location");

                    byte opt = 0;
                    Object fallOff = parameters.get("fallOff");
                    if (fallOff != null) {
                        opt |= 0b00000001; // bitwise time
                        used.add("fallOff");
                    }
                    out.write(opt);
                    if (fallOff != null) {
                        try {
                            out.write(ByteUtils.encodeFloat(Float.parseFloat(fallOff.toString())));
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("In file " + value.file + " the dialogue " + dialog.getKey() + " has fallOff which isn't a number");
                        }
                    }

                    for (Object key : parameters.keySet()) {
                        if (!(key instanceof String)) {
                            throw new RuntimeException("In file " + value.file + " the dialogue " + dialog.getKey() + " has a non String key parameter.");
                        }
                        if (!used.contains((String)key)) {
                            throw new RuntimeException("In file " + value.file + " the dialogue " + dialog.getKey() + " has the unused parameter " + key);
                        }
                    }

                } else {
                    throw new RuntimeException("In file " + value.file + " the dialogue " + dialog.getKey() + " has the wrong type " + dialog.getValue().getClass().getName() + ".");
                }
            }
        } else {
            throw new RuntimeException("In file " + value.file + " the dialog(s) don't have dialogues and instead have a " + section.getClass().getName());
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
