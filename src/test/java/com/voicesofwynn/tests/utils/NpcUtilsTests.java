package com.voicesofwynn.tests.utils;

import com.voicesofwynn.core.utils.LineUtils;
import org.junit.jupiter.api.Test;

public class NpcUtilsTests {

    @Test
    void nameFromLine() {
        assert LineUtils.npcNameFromLine("[1/2] Caravan Driver: Agh!")
                .equals("caravandriver");
    }
}
