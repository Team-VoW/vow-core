package com.voicesofwynn.tests;

import com.voicesofwynn.core.VOWCore;
import com.voicesofwynn.core.interfaces.IFunctionProvider;
import com.voicesofwynn.core.wrappers.VOWLocation;
import org.junit.jupiter.api.Test;

import java.io.File;

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
        }, new File("./tests/general/ull_test"));



    }
}
