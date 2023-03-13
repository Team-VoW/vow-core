package com.voicesofwynn.core;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
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

    public void save () throws IOException {
        yaml = new Yaml();
        yaml.dump(values, new FileWriter(settings));
    }

}
