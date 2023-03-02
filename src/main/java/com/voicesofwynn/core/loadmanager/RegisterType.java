package com.voicesofwynn.core.loadmanager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public interface RegisterType {

    void load(FileInputStream input) throws IOException;

    /**
     * @param writer file writer in to which to write the generated bytes
     * @param section section is the loaded yaml part that this type owns
     */
    void write(FileOutputStream writer, Object section, WriteInstanceValues instance) throws IOException;

    /**
     * @return true if the type is write time only
     */
    boolean isWriteTimeOnly();

    String getName();

}
