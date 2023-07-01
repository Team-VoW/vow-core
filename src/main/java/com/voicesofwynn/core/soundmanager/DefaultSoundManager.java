package com.voicesofwynn.core.soundmanager;

import com.voicesofwynn.core.VOWCore;
import com.voicesofwynn.core.sourcemanager.SourceManager;
import com.voicesofwynn.core.utils.WebUtil;
import com.voicesofwynn.core.wrappers.PlayEvent;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultSoundManager extends SoundManager {

    private final ConcurrentMap<String, SourceManager.RemoteFile> files = new ConcurrentHashMap<>();
    private final AtomicReference<String> last = new AtomicReference<>();

    private final Set<PlayEventBasically> toPlay = new HashSet<>();

    public static class PlayEventBasically {
        public String id;
        public PlayEvent event;


        public PlayEventBasically(String id, PlayEvent event) {
            this.id = id;
            this.event = event;
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
                            InputStream s = WebUtil.getHttpsStream("src" + code, rFile.sources);
                            Files.copy(s, rFile.file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            files.put(code.id, rFile);
                            sm.soundFiles.remove(code.id);
                            VOWCore.getFunctionProvider().playFileSound(rFile.file, code.event);
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
    public void playSound(String name, PlayEvent event) {
        last.set(name);
        SourceManager.RemoteFile file = files.get(name);
        if (file == null) {
            toPlay.add(new PlayEventBasically(name, event));
        } else {
            VOWCore.getFunctionProvider().playFileSound(file.file, event);
        }
    }


    @Override
    public float getProgress() {
        return (float) files.size() / (SourceManager.getInstance().soundFiles.size() + files.size());
    }
}
