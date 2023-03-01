package com.voicesofwynn.tests.loadmanager.typetests;

import com.voicesofwynn.TestSettings;
import com.voicesofwynn.core.loadmanager.LoadManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class DialogTypeTest {

    @Test
    void fileTest() throws IOException {
        LoadManager loadManager = new LoadManager();

        File testOut = new File(TestSettings.TEST_DIR, "loadManager/type/dialogTests/test_a");

        loadManager.build(new File("files/loadmanager/individual/dialog_tests/test_a.yml"),
                testOut);



        LoadManager.getInstance().load(testOut);

    }
}
