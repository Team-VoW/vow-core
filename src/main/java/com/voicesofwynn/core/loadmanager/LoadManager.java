package com.voicesofwynn.core.loadmanager;

import com.voicesofwynn.core.loadmanager.types.Types;
import com.voicesofwynn.core.utils.ByteUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for building and locating files that are used by the VoicesOfWynn mod.
 */

public class LoadManager {

    private static LoadManager instance;
    private final Map<String, RegisterType> register;

    public LoadManager () {
        register = new HashMap<>();
        instance = this;

        Types.init(this);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void build(File in, File out, File root) throws IOException {
        Yaml yaml = new Yaml();
        Map<String, Object> fl = yaml.load(Files.newInputStream(in.toPath()));
        if (out.exists()) {
            out.delete();
        }
        out.getParentFile().mkdirs();
        out.createNewFile();
        FileOutputStream output = new FileOutputStream(out);

        WriteInstanceValues values = new WriteInstanceValues();
        values.filePath = in.getPath();
        values.file = in;
        values.baseSoundDirectory = in.getParentFile();
        values.rootDir = root;

        for (Map.Entry<String, Object> entry : fl.entrySet()) {
            RegisterType type = register.get(entry.getKey());
            if (type == null) {
                throw new RuntimeException("Unknown type: " + entry.getKey());
            }
            if (!type.isWriteTimeOnly()) {
                output.write(ByteUtils.encodeString(type.getName()));
            }
            type.write(output, entry.getValue(), values);
        }
        output.close();
    }

    public void load(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        while (fileInputStream.available() > 0) {
            String typeName = ByteUtils.readString(fileInputStream);
            RegisterType type = register.get(typeName);
            if (type == null) {
                throw new RuntimeException("Unknown type: " + typeName);
            }
            type.load(fileInputStream);
        }
        fileInputStream.close();
    }

    public void register(RegisterType type) {
        register.put(type.getName(), type);
    }

    public static LoadManager getInstance() {
        return instance;
    }
}
