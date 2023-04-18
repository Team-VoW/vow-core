package com.voicesofwynn.core.sourcemanager;

import com.voicesofwynn.core.Options;
import com.voicesofwynn.core.VOWCore;
import com.voicesofwynn.core.utils.ByteUtils;
import com.voicesofwynn.core.utils.VOWLog;
import com.voicesofwynn.core.utils.WebUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;

public class SourceManager {

    private File base;

    private LinkedHashMap<String, String[]> sources;
    public static SourceManager getInstance() {
        return instance;
    }

    public static SourceManager instance;

    public SourceManager() {
        if (VOWCore.getRootFolder().getPath().equals(""))
            return;
        instance = this;

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
            }
        } else {
            sources.putAll(VOWCore.getFunctionProvider().defaultSources());
        }
    }

    public Map<String, Object> loadSourceEnables(File folder) {
        File options = new File(folder, "enables.yml");
        if (options.exists()) {
            Yaml yaml = new Yaml();
            try {
                return yaml.load(Files.newInputStream(options.toPath()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }

    public void update() {
        VOWCore.isWorking = false;
        for (Map.Entry<String, String[]> source : sources.entrySet()) {
            String name = source.getKey();
            Sources sources = new Sources(source.getValue());

            File root = new File(base, "sources/" + name);
            WebUtil util = new WebUtil();

            treeWalkUpdate(loadSourceEnables(root), false, "base", sources, root, util);

            while (neededTreeWalk.get() > doneTreeWalk.get()) {
                System.out.println(neededTreeWalk.get() + " - " + doneTreeWalk.get());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            if (Options.deleteUnneededFiles) {
                deleteUnknown(new File(root, "files"), "");
            }
        }
        VOWCore.isWorking = true;
    }

    public volatile AtomicInteger neededTreeWalk = new AtomicInteger(0);
    public volatile AtomicInteger doneTreeWalk = new AtomicInteger(0);

    public ConcurrentMap<String, RemoteFile> configFiles = new ConcurrentHashMap<>();
    public ConcurrentMap<String, RemoteFile> soundFiles = new ConcurrentHashMap<>();

    public void deleteUnknown(File root, String path) {
        File[] fs = root.listFiles();
        if (fs != null) {
            for (File f : fs) {
                String cur = path + "/" + f.getName();
                if (f.isDirectory()) {
                    deleteUnknown(f, cur);
                } else {
                    if (!configFiles.containsKey(cur) && !soundFiles.containsKey(cur)) {
                        System.out.println("Sniped out of existence " + cur);
                        f.delete();
                    }
                }
            }
        }



        fs = root.listFiles();
        if (fs != null && fs.length == 0) {
            System.out.println("Sniped out of existence " + path);
            root.delete();
        }
    }



    public static class RemoteFile {
        public Sources sources;

        public File file;

        public long hash;

        public RemoteFile(Sources sources, File file, long hash) {
            this.sources = sources;
            this.file = file;
            this.hash = hash;
        }
    }

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

    private boolean isChild(File child, File parent) {
        return (child.toPath().startsWith(parent.toPath()));
    }
    private void treeWalkUpdate(Map<String, Object> enabled, boolean everything_,
                                String currentPath, Sources sources, File root,
                                WebUtil util) {

        neededTreeWalk.addAndGet(1);
        util.getRemoteFile(
                "lists/" + currentPath.replaceAll("/", "\\$"),
                (got) -> {
                    boolean everything = everything_;
                    try {
                        while (got.read(new byte[0]) != -1) {
                            String name = ByteUtils.readString(got);
                            byte type = ByteUtils.readByte(got);
                            long hash = ByteUtils.readLong(got);

                            String path = currentPath + "/" + name;

                            File fl = new File(root,"files/" + path);

                            if (!isChild(fl, new File(root,"files"))) {
                                fl = new File(root,"files/" + currentPath + "/very-bad-name-" + new Random().nextFloat());
                            }


                            System.out.println("Test got " + name + " - " + type + " - " + hash);
                            boolean enabledBool = everything;
                            if (!everything) {
                                Object enabledObj = enabled.get(name);
                                if (enabledObj != null) {
                                    if (enabledObj.equals("*")) {
                                        everything = true;
                                    }
                                    enabledBool = true;
                                }
                            }
                            if (enabledBool) {
                                if (type == 1) {
                                    File localPath = new File(root, "lists/" + path.replaceAll("/", "\\$"));
                                    long hashLocal = readHash(localPath);
                                    System.out.println("Does local match? " + (hashLocal == hash));
                                    treeWalkUpdate(enabled, everything, path, sources, root, util);
                                } else {
                                    try {
                                        new File(root, "files/" + currentPath).mkdirs();
                                    } catch (Exception e) { // just in case
                                        e.printStackTrace();
                                    }

                                    if (name.endsWith(".vow-config")) {
                                        configFiles.put(path, new RemoteFile(
                                                sources,
                                                fl,
                                                hash
                                        ));
                                    } else {
                                        soundFiles.put(path, new RemoteFile(
                                                sources,
                                                fl,
                                                hash
                                        ));
                                    }
                                }
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
