package com.voicesofwynn.tests;

import com.voicesofwynn.TestSettings;
import com.voicesofwynn.core.VOWCore;
import com.voicesofwynn.core.interfaces.IFunctionProvider;
import com.voicesofwynn.core.sourcemanager.SourceManager;
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

    }
}
