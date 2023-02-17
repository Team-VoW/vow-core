package com.voicesofwynn.tests.utils.byteutil;

import com.voicesofwynn.TestSettings;
import com.voicesofwynn.core.utils.ByteUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class GenericTests {

    @Test
    void generalTest1() throws IOException {
        File f = new File(TestSettings.TEST_DIR, "byteUtils/generalTests1");
        f.getParentFile().mkdirs();
        if (f.exists())
            Files.delete(f.toPath());
        Files.createFile(f.toPath());
        FileOutputStream stream = new FileOutputStream(f);

        stream.write(ByteUtils.encodeInteger(1000));
        stream.write(ByteUtils.encodeInteger(100));
        stream.write(ByteUtils.encodeInteger(-100));
        stream.write(ByteUtils.encodeString("This is a string"));
        stream.write(ByteUtils.encodeInteger(100));
        stream.write(ByteUtils.encodeInteger(100));
        stream.write(ByteUtils.encodeString("This is a string 2.0"));
        stream.write(ByteUtils.encodeInteger(99));

        stream.close();

        FileInputStream inp = new FileInputStream(f);

        assert ByteUtils.readInteger(inp) == 1000;
        assert ByteUtils.readInteger(inp) == 100;
        assert ByteUtils.readInteger(inp) == -100;
        assert ByteUtils.readString(inp).equals("This is a string");
        assert ByteUtils.readInteger(inp) == 100;
        assert ByteUtils.readInteger(inp) == 100;
        assert ByteUtils.readString(inp).equals("This is a string 2.0");
        assert ByteUtils.readInteger(inp) == 99;

        inp.close();
    }

    @Test
    void generalTest2() throws IOException {
        File f = new File(TestSettings.TEST_DIR, "byteUtils/generalTests2");
        f.getParentFile().mkdirs();
        if (f.exists())
            Files.delete(f.toPath());
        Files.createFile(f.toPath());
        FileOutputStream stream = new FileOutputStream(f);

        stream.write(ByteUtils.encodeInteger(1000));
        stream.write(ByteUtils.encodeFloat(100.99f));
        stream.write(ByteUtils.encodeString("This is a string 2.0"));
        stream.write(ByteUtils.encodeInteger(99));

        stream.close();

        FileInputStream inp = new FileInputStream(f);

        assert ByteUtils.readInteger(inp) == 1000;
        assert ByteUtils.readFloat(inp) == 100.99f;
        assert ByteUtils.readString(inp).equals("This is a string 2.0");
        assert ByteUtils.readInteger(inp) == 99;

        inp.close();
    }
}
