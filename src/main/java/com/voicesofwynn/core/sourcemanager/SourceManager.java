package com.voicesofwynn.core.sourcemanager;

import com.voicesofwynn.core.VOWCore;
import com.voicesofwynn.core.utils.ByteUtils;
import com.voicesofwynn.core.utils.VOWLog;
import com.voicesofwynn.core.utils.WebUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;

public class SourceManager {

    private File base;

    private LinkedHashMap<String, String[]> sources;
    private Map<String, Map<String, Object>> sourcesEnables;

    public static SourceManager getInstance() {
        return instance;
    }

    public static SourceManager instance;

    public SourceManager() {
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
        Yaml yaml = new Yaml();
        Map<String, Object> config;
        try {
            config = yaml.load(new FileInputStream(setFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (config != null) {
            Object sourcesObj = config.get("sources");

            if (sourcesObj instanceof Map) {
                for (Map.Entry<String, Object> str : ((Map<String, Object>) sourcesObj).entrySet()) {
                    sources.put(str.getKey(), str.getValue().toString().split("\\|"));
                }

                loadSourceEnables();
                saveSourceEnables();
            }
        } else {
            sources.putAll(VOWCore.getFunctionProvider().defaultSources());
        }
    }

    public void loadSourceEnables() {
        for (Map.Entry<String, String[]> source : sources.entrySet()) {
            String name = source.getKey();
            File cfg = new File(base, name + "/enabled.yml");
            if (cfg.exists()) {
                Yaml yaml = new Yaml();
                try {
                    sourcesEnables.put(name, yaml.load(Files.newInputStream(cfg.toPath())));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                sourcesEnables.put(name, new LinkedHashMap<>());
            }
        }
    }

    public void saveSourceEnables() {
        for (Map.Entry<String, String[]> source : sources.entrySet()) {
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
        VOWCore.isWorking = false;
        for (Map.Entry<String, String[]> source : sources.entrySet()) {
            String name = source.getKey();
            Sources sources = new Sources(source.getValue());

            File root = new File(base, "sources/" + name);
            WebUtil util = new WebUtil();

            treeWalkUpdate(sourcesEnables.get(name), false, "base", sources, root, util);

            while (neededTreeWalk.get() > doneTreeWalk.get()) {
            }
        }
        VOWCore.isWorking = true;
    }

    public volatile AtomicInteger neededTreeWalk = new AtomicInteger(0);
    public volatile AtomicInteger doneTreeWalk = new AtomicInteger(0);

    public Map<String, String[]> configFiles = new HashMap<>();
    public Map<String, String[]> soundFiles = new HashMap<>();

    public static long readHash(File f) {
        if (!f.exists()) {
            return -1;
        }
        if (f.isDirectory()) {
            return -1;
        }
        CRC32 crc32 = new CRC32();
        try {
            crc32.update(Files.readAllBytes(f.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return crc32.getValue();
    }
    private void treeWalkUpdate(Map<String, Object> enabled, boolean everything,
                                String currentPath, Sources sources, File root,
                                WebUtil util) {

        neededTreeWalk.addAndGet(1);
        util.getRemoteFile(
                "lists/" + currentPath,
                (got) -> {
                    try {
                        while (got.read(new byte[0]) != -1) {
                            String name = ByteUtils.readString(got);
                            byte type = ByteUtils.readByte(got);
                            long hash = ByteUtils.readLong(got);

                            System.out.println("Test got " + name + " - " + type + " - " + hash);

                            if (type == 1) {
                                String path = currentPath + "$" + name;
                                File localPath = new File(root, "lists/" + path);
                                long hashLocal = readHash(localPath);
                                System.out.println("Does local match? " + (hashLocal == hash));
                                treeWalkUpdate(enabled, everything, path, sources, root, util);
                            } else {

                            }
                        }
                        got.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        VOWLog.log("Failed to download " + "lists/" + currentPath);
                    }
                    doneTreeWalk.addAndGet(1);
                },
                sources
        );

    }

    public void load() {



    }

}
