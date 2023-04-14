package com.voicesofwynn.tests.utils;

import com.voicesofwynn.TestSettings;
import com.voicesofwynn.core.utils.WebUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class WebUtilTests {
    @Test
    void getTest1() {

        WebUtil util = new WebUtil();

        util.getRemoteFile("tests/byteUtils/strTests",  (contents -> {
            System.out.println("strTests");
            System.out.println(Arrays.toString(contents));
                })
                , new String[] {TestSettings.baseTestURL});

        while (util.finished() < 1) {

        }

    }
}
