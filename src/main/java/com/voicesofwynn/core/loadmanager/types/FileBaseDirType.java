package com.voicesofwynn.core.loadmanager.types;

import com.voicesofwynn.core.loadmanager.RegisterType;
import com.voicesofwynn.core.loadmanager.WriteInstanceValues;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileBaseDirType implements RegisterType {

    @Override
    public void load(FileInputStream reader) {

    }

    @Override
    public void write(FileOutputStream writer, Object section, WriteInstanceValues values) {
        if (!(section instanceof String)) {
            throw new RuntimeException("In file " + values.file + ", the file-base-dir is set to a " + section.getClass().getName() + " instead of a String.");
        }
        File f = new File(values.file.getParentFile(), (String)section);
        if (!f.exists()) {
            throw new RuntimeException("In file " + values.file + ", the file-base-dir is set to folder " + f.getPath() + " which does not exist.");
        }
        if (!f.isDirectory()) {
            throw new RuntimeException("In file " + values.file + ", the file-base-dir is set to " + f.getPath() + " which is a file and not a directory.");
        }

        values.baseSoundDirectory = f;
    }

    @Override
    public boolean isWriteTimeOnly() {
        return true;
    }

    @Override
    public String getName() {
        return "file-base-dir";
    }
}
