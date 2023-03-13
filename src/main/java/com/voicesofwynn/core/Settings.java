package com.voicesofwynn.core;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class Settings {

    public Settings () {
        File settings = new File(VOWCore.getRootFolder(), "settings.yml");

        Yaml yaml = new Yaml();
        try {
            Map<String, Object> fl = yaml.load(Files.newInputStream(settings.toPath()));



            yaml.dump(fl, new FileWriter(settings));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
