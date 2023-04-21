package com.voicesofwynn.core.soundmanager;

import com.voicesofwynn.core.VOWCore;
import com.voicesofwynn.core.sourcemanager.SourceManager;
import com.voicesofwynn.core.utils.WebUtil;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultSoundManager extends SoundManager {

    private volatile ConcurrentMap<String, SourceManager.RemoteFile> files = new ConcurrentHashMap<>();
    private AtomicReference<String> last = new AtomicReference<>();

    private ConcurrentSkipListSet<String> toPlay = new ConcurrentSkipListSet<>();

    private Thread thread;

    @Override
    public void start() {
        if (thread != null) {
            thread.stop();
        }
        thread = new Thread(() -> {
            SourceManager sm = SourceManager.getInstance();

            for (Map.Entry<String, SourceManager.RemoteFile> file : sm.soundFiles.entrySet()) {
                if (SourceManager.readHash(file.getValue().file) == file.getValue().hash) {
                    files.put(file.getKey(), file.getValue());
                    sm.soundFiles.remove(file.getKey());
                }
            }

            while (sm.soundFiles.size() > 0) {
                for (String code : toPlay) {
                    SourceManager.RemoteFile rFile = sm.soundFiles.get(code);
                    if (rFile != null) {
                        try {
                            rFile.file.getParentFile().mkdirs();
                            InputStream s = WebUtil.getHttpStream("src/" + code.substring(1), rFile.sources);
                            Files.copy(s, rFile.file.toPath());
                            files.put(code, rFile);
                            sm.soundFiles.remove(code);
                            VOWCore.getFunctionProvider().playFileSound(rFile.file);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                WebUtil util = new WebUtil();
                int amount = 0;
                String last = this.last.get();
                if (last != null) {
                    last = last.substring(0, last.lastIndexOf("/"));
                    for (Map.Entry<String, SourceManager.RemoteFile> file : sm.soundFiles.entrySet()) {
                        if (amount > 6) {
                            break;
                        }
                        if (file.getKey().contains(last)) {
                            util.getRemoteFile(
                                    "src" + file.getKey(),
                                    (got) -> {
                                        file.getValue().file.getParentFile().mkdirs();
                                        try {
                                            Files.copy(got, file.getValue().file.toPath());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        sm.soundFiles.remove(file.getKey());
                                        files.put(file.getKey(), file.getValue());
                                    },
                                    file.getValue().sources
                            );
                            amount += 1;
                        }
                    }
                } else {
                    for (Map.Entry<String, SourceManager.RemoteFile> file : sm.soundFiles.entrySet()) {
                        if (amount > 6) {
                            break;
                        }
                        util.getRemoteFile(
                                "src" + file.getKey(),
                                (got) -> {
                                    try {
                                        file.getValue().file.getParentFile().mkdirs();
                                        Files.copy(got, file.getValue().file.toPath());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    sm.soundFiles.remove(file.getKey());
                                    files.put(file.getKey(), file.getValue());
                                },
                                file.getValue().sources
                        );
                        amount += 1;
                    }
                }
                while (util.finished() < amount) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void playSound(String name) {
        last.set(name);
    }


    @Override
    public float getProgress() {
        return (float) files.size() / (SourceManager.getInstance().soundFiles.size() + files.size());
    }
}
