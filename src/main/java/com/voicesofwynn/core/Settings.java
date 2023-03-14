package com.voicesofwynn.core;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Settings {

    private Map<String, Object> values;
    private File settings;

    private Yaml yaml;

    public Settings (File settings) {
        this.settings = settings;

        yaml = new Yaml();
        try {
            values = yaml.load(Files.newInputStream(settings.toPath()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Object readValue(String key) {
        String[] split = key.split("\\.");
        Object value = values;
        for (String loc : split) {
            if (value instanceof Map<?, ?>) {
                value = ((Map<?, ?>) value).get(loc);
                if (value == null) {
                    return null;
                }
            }
        }
        return value;
    }

    public List<String> readChildren(String key) {
        Object val = readValue(key);
        key += ".";
        List<String> keys = new LinkedList<>();
        if (val instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) val;
            for (Object object : map.keySet()) {
                if (object instanceof String) {
                    keys.add(key + object);
                } else {
                    return null;
                }
            }

        } else {
            return null;
        }
        return keys;
    }

    public void save () throws IOException {
        yaml = new Yaml();
        yaml.dump(values, new FileWriter(settings));
    }

}
