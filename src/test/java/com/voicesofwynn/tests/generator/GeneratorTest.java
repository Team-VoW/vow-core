package com.voicesofwynn.tests.generator;

import com.voicesofwynn.core.generator.Generator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class GeneratorTest {
    @Test
    void test_a() throws IOException {
        Generator.generate(new File("./files/generator/test_a"), new String[0]);
    }
}
