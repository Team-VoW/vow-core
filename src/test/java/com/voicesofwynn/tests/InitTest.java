package com.voicesofwynn.tests;

import com.voicesofwynn.TestSettings;
import com.voicesofwynn.core.VOWCore;
import com.voicesofwynn.core.interfaces.IFunctionProvider;
import com.voicesofwynn.core.soundmanager.DefaultSoundManager;
import com.voicesofwynn.core.soundmanager.SoundManager;
import com.voicesofwynn.core.sourcemanager.SourceManager;
import com.voicesofwynn.core.wrappers.PlayEvent;
import com.voicesofwynn.core.wrappers.VOWLocation;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class InitTest {
    @Test
    void init() {
        VOWCore.init(new IFunctionProvider() {
            @Override
            public void playFileSound(File file, PlayEvent event) {

            }

            @Override
            public VOWLocation getNpcLocationFromName(String name) {
                return null;
            }

            @Override
            public VOWLocation getPlayerLocation() {
                return null;
            }

            @Override
            public Map<String, String[]> defaultSources() {
                return new HashMap<String, String[]>() {
                    {
                        put("main", new String[]{TestSettings.baseTestURL});
                    }
                };
            }
        }, new File("./tests/general/ull_test"));

        SourceManager.getInstance().update();

        while (!VOWCore.isWorking()) {

        }

        SourceManager.getInstance().reload();

        SoundManager.instance = new DefaultSoundManager();
        SoundManager.instance.start();

        while (SoundManager.instance.getProgress() < 1.0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Map<String, Object> tree = SourceManager.instance.obtainTree("main");
        System.out.println(tree);

    }
}
