package com.voicesofwynn.core.sourcemanager;

import com.voicesofwynn.core.Options;
import com.voicesofwynn.core.VOWCore;
import com.voicesofwynn.core.loadmanager.LoadManager;
import com.voicesofwynn.core.utils.ByteUtils;
import com.voicesofwynn.core.utils.VOWLog;
import com.voicesofwynn.core.utils.WebUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;

/**
 * Handles downloading sound files, along with cleaning up already played sounds
 */

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

            treeWalkUpdate(loadSourceEnables(root), false, "", sources, root, util);

            while (neededTreeWalk.get() > doneTreeWalk.get()) {
                
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            this.downloadConfigsFunction();
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
                        f.delete();
                    }
                }
            }
        }



        fs = root.listFiles();
        if (fs != null && fs.length == 0) {
            
            root.delete();
        }
    }

    public Map<String, Object> obtainTree(String nameOfSource) {
        Sources src = new Sources(sources.get(nameOfSource));
        ConcurrentMap<String, Object> map = new ConcurrentHashMap<>();
        WebUtil util = new WebUtil();
        leftToTreeWalk = new AtomicInteger();

        obtainTreeWalk(src, "", util, map);

        while (leftToTreeWalk.get() > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return map;
    }

    private AtomicInteger leftToTreeWalk = new AtomicInteger();

    public void obtainTreeWalk(Sources sources, String path, WebUtil util, ConcurrentMap<String, Object> map) {
        leftToTreeWalk.addAndGet(1);
        util.getRemoteFile(
                "lists/base" + path,
                (got) -> {try {
                    while (got.read(new byte[0]) != -1) {
                        String name = ByteUtils.readString(got);
                        byte type = ByteUtils.readByte(got);
                        long hash = ByteUtils.readLong(got);
                        if (type == 0) {
                            map.put(name, hash);
                        } else {
                            ConcurrentMap<String, Object> child = new ConcurrentHashMap<>();
                            map.put(name, child);
                            obtainTreeWalk(sources, path + "$" + name, util, child);
                        }
                    }
                    leftToTreeWalk.addAndGet(-1);
                } catch (Exception e) {
                    e.printStackTrace();
                }},
                sources
        );
    }

    public void downloadConfigsFunction() {
        WebUtil util = new WebUtil();
        int started = 0;

        for (Map.Entry<String, RemoteFile> entry : configFiles.entrySet()) {
            RemoteFile file = entry.getValue();
            if (!file.file.exists() || readHash(file.file) != file.hash) {
                util.getRemoteFile(
                        "src/" + entry.getKey().substring(1),
                        (got) -> {
                            try {
                                Files.copy(got, file.file.toPath());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        file.sources
                );
                started++;
            }
        }

        while (util.finished() < started) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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

    private void walkOfflineTree(Map<String, Object> enabled, boolean everything_,
                                 String currentPath, Sources sources, File root) {
        File ops = new File(root, "lists/base" + currentPath.replaceAll("/", "\\$"));
        try {
            InputStream stream = Files.newInputStream(ops.toPath());
            while (stream.available() > 0) {
                boolean everything = everything_;
                String name = ByteUtils.readString(stream);
                byte type = ByteUtils.readByte(stream);
                long hash = ByteUtils.readLong(stream);

                String path = currentPath + "/" + name;

                File fl = new File(root, "files/" + path);

                if (!isChild(fl, new File(root, "files"))) {
                    fl = new File(root, "files/" + currentPath + "/very-bad-name-" + new Random().nextFloat());
                }

                Map<String, Object> en = null;
                boolean enabledBool = everything;
                if (!everything) {
                    Object enabledObj = enabled.get(name);
                    if (enabledObj != null) {
                        if (enabledObj.equals("*")) {
                            everything = true;
                        } else if (enabledObj instanceof Map) {
                            en = (Map<String, Object>) enabledObj;
                        }
                        enabledBool = true;
                    }
                }
                if (enabledBool) {
                    if (type == 1) {
                        walkOfflineTree(en, everything, path, sources, root);
                    } else {
                        try {
                            new File(root, "files" + currentPath).mkdirs();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void treeWalkUpdate(Map<String, Object> enabled, boolean everything_,
                                String currentPath, Sources sources, File root,
                                WebUtil util) {

        neededTreeWalk.addAndGet(1);
        util.getRemoteFile(
                "lists/base" + currentPath.replaceAll("/", "\\$"),
                (got) -> {
                    try {
                        File ops = new File(root, "lists/base" + currentPath.replaceAll("/", "\\$"));
                        ops.getParentFile().mkdirs();
                        Files.copy(got, ops.toPath(), StandardCopyOption.REPLACE_EXISTING);

                        InputStream stream = Files.newInputStream(ops.toPath());
                        while (stream.available() > 0) {
                            boolean everything = everything_;
                            String name = ByteUtils.readString(stream);
                            byte type = ByteUtils.readByte(stream);
                            long hash = ByteUtils.readLong(stream);

                            String path = currentPath + "/" + name;

                            File fl = new File(root,"files/" + path);

                            if (!isChild(fl, new File(root,"files"))) {
                                fl = new File(root,"files/" + currentPath + "/very-bad-name-" + new Random().nextFloat());
                            }

                            Map<String, Object> en = null;
                            boolean enabledBool = everything;
                            if (!everything) {
                                Object enabledObj = enabled.get(name);
                                if (enabledObj != null) {
                                    if (enabledObj.equals("*")) {
                                        everything = true;
                                    } else if (enabledObj instanceof Map) {
                                        en = (Map<String, Object>) enabledObj;
                                    }
                                    enabledBool = true;
                                }
                            }


                            if (enabledBool) {
                                if (type == 1) {
                                    File localPath = new File(root, "lists/base" + path.replaceAll("/", "\\$"));
                                    long hashLocal = readHash(localPath);
                                    if (hashLocal != hash) {

                                        treeWalkUpdate(en, everything, path, sources, root, util);

                                    } else {


                                        walkOfflineTree(en, everything, path, sources, root);


                                    }
                                } else {
                                    try {
                                        new File(root, "files" + currentPath).mkdirs();
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
                        VOWLog.log("Failed to download " + "lists/base" + currentPath.replaceAll("/", "\\$"));
                    }

                    doneTreeWalk.addAndGet(1);
                },
                sources
        );

    }

    public void reload() {
        for (Map.Entry<String, String[]> source : sources.entrySet()) {
            String name = source.getKey();
            File root = new File(base, "sources/" + name);
            loadDir(root);
        }
    }

    public void loadDir(File f) {
        File[] fs = f.listFiles();
        if (fs == null)
            return;
        for (File file : fs) {
            if (file.isDirectory()) {
                loadDir(file);
            } else if (file.getName().endsWith(".vow-config")) {
                try {
                    LoadManager.getInstance().load(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
