package com.voicesofwynn.core.loader;

import com.voicesofwynn.core.Settings;
import com.voicesofwynn.core.VOWCore;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

public class Loader {

    private Settings config;
    private File base;

    private LinkedHashMap<String, String> sources;
    private LinkedHashMap<String, Map<String, Object>> sourcesEnables;
    public Loader () {
        if (VOWCore.getRootFolder().getPath().equals(""))
            return;

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
        config = new Settings(setFile);

        for (String str : config.readChildren("sources")) {
            String val = config.readString(str, "non");
            if (val.equals("non")) {
                return;
            }
            String[] split = str.split("\\.");
            sources.put(split[split.length-1], val);
        }

        sources.putAll(VOWCore.getFunctionProvider().defaultSources());

        loadSourceEnables();
    }

    public void loadSourceEnables() {
        for (Map.Entry<String, String> source : sources.entrySet()) {
            String name = source.getKey();
            File cfg = new File(base, "sources/" + name + "/enabled.yml");
            Yaml yaml = new Yaml();
            try {
                sourcesEnables.put(name, yaml.load(new FileInputStream(cfg)));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void saveSourceEnables() {
        for (Map.Entry<String, String> source : sources.entrySet()) {
            String name = source.getKey();
            File cfg = new File(base, "sources/" + name + "/enabled.yml");
            cfg.getParentFile().mkdirs();
            Yaml yaml = new Yaml();
            try {
                yaml.dump(cfg, new FileWriter(cfg));
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
