package com.voicesofwynn.core.loader;

import com.voicesofwynn.core.Settings;
import com.voicesofwynn.core.VOWCore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;

public class Loader {

    private Settings config;
    private File base;

    private LinkedHashMap<String, String> sources;
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

    }

}
