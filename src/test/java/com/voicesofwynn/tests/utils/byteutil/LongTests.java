package com.voicesofwynn.tests.utils.byteutil;

import com.voicesofwynn.TestSettings;
import com.voicesofwynn.core.utils.ByteUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class LongTests {

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    @Test
    void readWriteTest() throws IOException {
        long[] testInts = new long[] {
                1000,
                -100,
                Integer.MAX_VALUE,
                Integer.MIN_VALUE,
                1235982,
                124192,
                68535,
                34634,
                -3953962,
                -3252,
                -125321,
                -999,
                10000000000L,
                100000000000L,
                100000000454550L,
                -1000004325525550000L,
                Long.MAX_VALUE,
                Long.MIN_VALUE
        };

        File f = new File(TestSettings.TEST_DIR, "byteUtils/longTests");
        f.getParentFile().mkdirs();
        if (f.exists())
            Files.delete(f.toPath());
        Files.createFile(f.toPath());
        FileOutputStream stream = new FileOutputStream(f);

        for (long i : testInts) {
            stream.write(ByteUtils.encodeLong(i));
        }
        stream.close();

        FileInputStream inp = new FileInputStream(f);

        for (long i : testInts) {
            assert ByteUtils.readLong(inp) == i;
        }
        inp.close();

    }
}
