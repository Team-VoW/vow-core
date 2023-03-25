package com.voicesofwynn.core.loader;

import com.voicesofwynn.core.Settings;
import com.voicesofwynn.core.VOWCore;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Loader {

    private File base;

    private LinkedHashMap<String, String> sources;
    private Map<String, Map<String, Object>> sourcesEnables;

    public static Loader getInstance() {
        return instance;
    }

    public static Loader instance;

    public Loader () {
        if (VOWCore.getRootFolder().getPath().equals(""))
            return;
        instance = this;

        sourcesEnables = new HashMap<>();
        sources = new LinkedHashMap<>();

        base = new File(VOWCore.getRootFolder(), "files");
        File setFile = new File(VOWCore.getRootFolder(), "source_settings.yml");
        if (!setFile.exists()) {
            try {
                setFile.getParentFile().mkdirs();
                Files.createFile(setFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Settings config = new Settings(setFile);

        for (String str : config.readChildren("sources")) {
            String val = config.readString(str, "non");
            if (val.equals("non")) {
                continue;
            }
            String[] split = str.split("\\.");
            sources.put(split[split.length-1], val);
        }

        sources.putAll(VOWCore.getFunctionProvider().defaultSources());

        loadSourceEnables();
        saveSourceEnables();
    }

    public void loadSourceEnables() {
        for (Map.Entry<String, String> source : sources.entrySet()) {
            String name = source.getKey();
            File cfg = new File(base, name + "/enabled.yml");
            if (cfg.exists()) {
                Yaml yaml = new Yaml();
                try {
                    sourcesEnables.put(name, yaml.load(new FileInputStream(cfg)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                sourcesEnables.put(name, new LinkedHashMap<>());
            }
        }
    }

    public void saveSourceEnables() {
        for (Map.Entry<String, String> source : sources.entrySet()) {
            String name = source.getKey();
            File cfg = new File(base, name + "/enabled.yml");
            cfg.getParentFile().mkdirs();
            Yaml yaml = new Yaml();
            try {
                yaml.dump(sourcesEnables.get(name), new FileWriter(cfg));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void update() {
        for (Map.Entry<String, String> source : sources.entrySet()) {
            String name = source.getKey();
            String link = source.getValue();

            File root = new File(base, "sources/" + name);



        }

    }

    public void load() {



    }

}
