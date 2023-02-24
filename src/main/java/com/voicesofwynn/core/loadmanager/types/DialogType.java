package com.voicesofwynn.core.loadmanager.types;

import com.voicesofwynn.core.loadmanager.RegisterType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;

public class DialogType implements RegisterType {
    @Override
    public void load(FileInputStream reader) {

    }

    @Override
    public void write(FileOutputStream writer, Object section, Map<String, Object> vars) {
    }

    @Override
    public String getName() {
        return "dialog";
    }
}
