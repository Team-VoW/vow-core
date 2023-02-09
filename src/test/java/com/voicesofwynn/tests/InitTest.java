package com.voicesofwynn.tests;

import com.voicesofwynn.core.VOWCore;
import org.junit.jupiter.api.Test;

public class InitTest {
    @Test
    void init() {
        // fail test
        try {
            VOWCore.init(null, null);
            assert false;
        } catch (IllegalArgumentException ignore) {

        }

    }
}
