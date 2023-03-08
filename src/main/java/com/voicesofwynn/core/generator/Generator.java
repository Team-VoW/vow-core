package com.voicesofwynn.core.generator;

import com.voicesofwynn.core.utils.VOWLog;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class Generator {

    public static void main(String[] args) {

    }

    /**
     * The generate function
     * <br><br>
     * Will generate output in the out folder which can be used as a preset source. <p>
     * Requires presets.yml in the in folder.
     *
     * @param base base project folder
     * @param in the files/folders to generate (if non, then it will just generate everything)
     */
    public static void generate(File base, String[] in) throws IOException {
        File settings = new File(base, "settings.yml");

        Map<String, Object> settingsInfo;
        if (!settings.isFile()) {
            VOWLog.log("Unable to find settings.yml in " + base.getPath() + " using defaults.");
            settingsInfo = new HashMap<>();
            settingsInfo.put("in", "src");
            settingsInfo.put("out", "out");
        } else {
            Yaml yaml = new Yaml();
            settingsInfo = yaml.load(Files.newInputStream(settings.toPath()));
            settingsInfo.putIfAbsent("in", "src");
            settingsInfo.putIfAbsent("out", "out");
        }


        if (in == null || in.length == 0) {
            in = new String[] {"."};
        }

    }




}
