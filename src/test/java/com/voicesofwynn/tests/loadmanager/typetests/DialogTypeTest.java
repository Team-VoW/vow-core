package com.voicesofwynn.tests.loadmanager.typetests;

import com.voicesofwynn.TestSettings;
import com.voicesofwynn.core.loadmanager.LoadManager;
import com.voicesofwynn.core.registers.DialogueRegister;
import com.voicesofwynn.core.utils.LineUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DialogTypeTest {

    @Test
    void fileTest() throws IOException {
        LoadManager loadManager = new LoadManager();

        File testOut = new File(TestSettings.TEST_DIR, "loadManager/type/dialogTests/test_a");

        loadManager.build(new File("files/loadmanager/individual/dialog_tests/test_a.yml"),
                testOut);

        LoadManager.getInstance().load(testOut);

        assert DialogueRegister.getInstance().getDialogs().get(LineUtils.lineFromMessage("[1/2] Caravan Driver: Agh!")) != null;
        assert DialogueRegister.getInstance().getDialogs().get(LineUtils.lineFromMessage("[3/2] Caravan Driver: This dialogue is not real")) == null;

    }
}
