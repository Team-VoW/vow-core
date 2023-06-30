package com.voicesofwynn.core.loadmanager.subtypes;

import com.voicesofwynn.core.loadmanager.WriteInstanceValues;
import com.voicesofwynn.core.utils.ByteUtils;
import com.voicesofwynn.core.utils.VOWLog;
import com.voicesofwynn.core.wrappers.FilePathProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/***
 *
 */
public class FileLocationSubType {

    public static void writeFileLocation(FileOutputStream out, Object part, WriteInstanceValues values, String context) throws IOException {
        if (part == null) {
            throw new RuntimeException("In file " + values.filePath + " there is a missing file for " + context);
        }
        if (part instanceof String) {
            File file = new File(values.baseSoundDirectory, (String)part);
            if (!file.isFile() && !part.equals("TODO")) {
                throw new RuntimeException("In file " + values.filePath + " the file for " + context + " is set to a non-existent file " + part);
            }
            if (part.equals("TODO")) {
                VOWLog.warn("TODO set in file "  + values.filePath + " the file for " + context);
            }

            out.write(0);
            out.write(ByteUtils.encodeString(values.rootDir.toURI().relativize(file.toURI()).getPath()));
        } else {

            out.write(255);
        }
    }

    public static FilePathProvider readFileLocation(FileInputStream in) throws IOException {

        // https://github.com/Team-VoW/vow-core/wiki/Types-in-code#file
        switch (ByteUtils.readByte(in)) {
            default:
                String path = ByteUtils.readString(in);
                return () -> path;

        }
    }
}
