package com.voicesofwynn.core.loadmanager;

import com.voicesofwynn.core.loadmanager.types.Types;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class LoadManager {

    private static LoadManager instance;
    private Map<String, RegisterType> register;

    public static final String VERSION = "0.1";

    public LoadManager () {
        register = new HashMap<>();
        instance = this;

        Types.init(this);
    }

    public void build(File in, File out) throws IOException {
        Yaml yaml = new Yaml();
        Map<String, Object> fl = yaml.load(Files.newInputStream(in.toPath()));
        FileOutputStream output = new FileOutputStream(out);

        Map<String, Object> variables = new HashMap<>();

        for (Map.Entry<String, Object> e : fl.entrySet()) {
            if (e.getValue() instanceof Map) {
                RegisterType type = register.get(e.getKey());
                if (type == null) {
                    throw new RuntimeException("Unknown type: " + e.getKey());
                }
                type.write(output, e.getValue(), variables);
            } else {
                variables.put(e.getKey(), e.getValue());
            }
        }
    }

    public void register(RegisterType type) {
        register.put(type.getName(), type);
    }

    public static LoadManager getInstance() {
        return instance;
    }
}
