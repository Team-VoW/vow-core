package com.voicesofwynn.core.loadmanager;

import com.voicesofwynn.core.loadmanager.types.Types;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class LoadManager {

    private static LoadManager instance;
    private Map<String, RegisterType> register;

    public LoadManager () {
        register = new HashMap<>();
        instance = this;

        Types.init(this);
    }

    public void build(File in, File out) throws IOException {
        Yaml yaml = new Yaml();
        Map<String, Object> fl = yaml.load(Files.newInputStream(in.toPath()));
        OutputStream output = Files.newOutputStream(out.toPath());

        for (Map.Entry<String, Object> e : fl.entrySet()) {

        }
    }

    public void register(RegisterType type) {
        register.put(type.getName(), type);
    }

    public static LoadManager getInstance() {
        return instance;
    }
}
