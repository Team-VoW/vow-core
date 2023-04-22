package com.voicesofwynn.core.soundmanager;

import com.voicesofwynn.core.VOWCore;
import com.voicesofwynn.core.sourcemanager.SourceManager;
import com.voicesofwynn.core.utils.WebUtil;
import com.voicesofwynn.core.wrappers.VOWLocationProvider;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultSoundManager extends SoundManager {

    private volatile ConcurrentMap<String, SourceManager.RemoteFile> files = new ConcurrentHashMap<>();
    private AtomicReference<String> last = new AtomicReference<>();

    private ConcurrentSkipListSet<PlayEventBasically> toPlay = new ConcurrentSkipListSet<>();

    public class PlayEventBasically {
        public String id;
        public VOWLocationProvider location;


        public PlayEventBasically(String id, VOWLocationProvider location) {
            this.id = id;
            this.location = location;
        }
    }

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
                for (PlayEventBasically code : toPlay) {
                    SourceManager.RemoteFile rFile = sm.soundFiles.get(code.id);
                    if (rFile != null) {
                        try {
                            rFile.file.getParentFile().mkdirs();
                            InputStream s = WebUtil.getHttpStream("src" + code, rFile.sources);
                            Files.copy(s, rFile.file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            files.put(code.id, rFile);
                            sm.soundFiles.remove(code.id);
                            VOWCore.getFunctionProvider().playFileSound(rFile.file, code.location);
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
                                            Files.copy(got, file.getValue().file.toPath(), StandardCopyOption.REPLACE_EXISTING);
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
                    if (amount < 6) {
                        this.last.set(null);
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
    public void playSound(String name, VOWLocationProvider location) {
        last.set(name);
        SourceManager.RemoteFile file = files.get(name);
        if (file == null) {
            toPlay.add(new PlayEventBasically(name, location));
        } else {
            VOWCore.getFunctionProvider().playFileSound(file.file, location);
        }
    }


    @Override
    public float getProgress() {
        return (float) files.size() / (SourceManager.getInstance().soundFiles.size() + files.size());
    }
}
