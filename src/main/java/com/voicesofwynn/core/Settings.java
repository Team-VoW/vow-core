package com.voicesofwynn.core;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
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
            } else {
                return null;
            }
        }
        return value;
    }

    public void setValue(String key, Object obj) {
        String[] split = key.split("\\.");
        Map<String, Object> value = values;
        for (int i = 0 ; i < split.length - 1 ; i++) {
            String loc = split[i];
            Object tValue = value.computeIfAbsent(loc, k -> new HashMap<String, Object>());
            if (tValue instanceof Map) {
                value = (Map<String, Object>) tValue;
            } else {
                tValue = new HashMap<>();
                value.put(loc, tValue);
                value = (Map<String, Object>) tValue;
            }
        }
        value.put(split[split.length - 1], obj);
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

    public String readString(String key, String defaultValue) {
        Object val = readValue(key);

        if (val instanceof String) {
            return (String) val;
        }
        setValue(key, defaultValue);
        return defaultValue;
    }

    public int readInteger(String key, int defaultValue) {
        Object val = readValue(key);

        if (val instanceof Integer) {
            return (int) val;
        }
        setValue(key, defaultValue);
        return defaultValue;
    }

    public float readFloat(String key, float defaultValue) {
        Object val = readValue(key);

        if (val instanceof Float) {
            return (float) val;
        }
        setValue(key, defaultValue);
        return defaultValue;
    }

    public boolean readBoolean(String key, boolean defaultValue) {
        Object val = readValue(key);

        if (val instanceof Boolean) {
            return (boolean) val;
        }
        setValue(key, defaultValue);
        return defaultValue;
    }

    public void save () throws IOException {
        yaml = new Yaml();
        yaml.dump(values, new FileWriter(settings));
    }

}
