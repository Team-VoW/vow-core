package com.voicesofwynn.tests.utils.byteutil;

import com.voicesofwynn.TestSettings;
import com.voicesofwynn.core.utils.ByteUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class IntTests {

    @SuppressWarnings({"ResultOfMethodCallIgnored", "resource"})
    @Test
    void readWriteTest() throws IOException {
        int[] testInts = new int[] {
                1000,
                -100,
                Integer.MAX_VALUE,
                Integer.MIN_VALUE,
                1235982,
                124192,
                68535,
                25367,
                346734,
                6354,
                34634,
                -3953962,
                -3252,
                -125321,
                -999
        };

        File f = new File(TestSettings.TEST_DIR, "byteUtils/intTests");
        f.getParentFile().mkdirs();
        if (f.exists())
            Files.delete(f.toPath());
        Files.createFile(f.toPath());
        FileOutputStream stream = new FileOutputStream(f);

        for (int i : testInts) {
            stream.write(ByteUtils.encodeInteger(i));
        }
        stream.close();

        FileInputStream inp = new FileInputStream(f);

        for (int i : testInts) {
            assert ByteUtils.readInteger(inp) == i;
        }
        inp.close();

    }
}
