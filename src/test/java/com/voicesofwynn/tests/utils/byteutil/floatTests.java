package com.voicesofwynn.tests.utils.byteutil;

import com.voicesofwynn.TestSettings;
import com.voicesofwynn.core.utils.ByteUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class floatTests {

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    @Test
    void readWriteTest() throws IOException {
        float[] testFloats = new float[] {
                10.125f,
                Float.MAX_VALUE,
                Float.MIN_VALUE,
                -120.151961f,
                125.124f
        };

        File f = new File(TestSettings.TEST_DIR, "byteUtils/floatTests");
        f.getParentFile().mkdirs();
        if (f.exists())
            Files.delete(f.toPath());
        Files.createFile(f.toPath());
        FileOutputStream stream = new FileOutputStream(f);

        for (float fl : testFloats) {
            stream.write(ByteUtils.encodeFloat(fl));
        }
        stream.close();

        FileInputStream inp = new FileInputStream(f);

        for (float fl : testFloats) {
            assert ByteUtils.readFloat(inp) == fl;
        }
        inp.close();

    }
}
