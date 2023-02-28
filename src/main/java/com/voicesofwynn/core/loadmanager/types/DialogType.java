package com.voicesofwynn.core.loadmanager.types;

import com.voicesofwynn.core.loadmanager.RegisterType;
import com.voicesofwynn.core.loadmanager.WriteInstanceValues;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class DialogType implements RegisterType {
    @Override
    public void load(FileInputStream reader) {

    }

    @Override
    public void write(FileOutputStream writer, Object section, WriteInstanceValues value) {

    }

    @Override
    public String getName() {
        return "dialog";
    }
}
