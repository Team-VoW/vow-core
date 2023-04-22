package com.voicesofwynn.tests.loadmanager.typetests;

import com.voicesofwynn.TestSettings;
import com.voicesofwynn.core.VOWCore;
import com.voicesofwynn.core.loadmanager.LoadManager;
import com.voicesofwynn.core.registers.DialogueRegister;
import com.voicesofwynn.core.utils.LineUtils;
import com.voicesofwynn.core.wrappers.VOWLocation;
import com.voicesofwynn.testing.EmptyFunctionProvider;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class DialogTypeTest {

    @Test
    void fileTest() throws IOException {
        LoadManager loadManager = new LoadManager();

        File testOut = new File(TestSettings.TEST_DIR, "loadManager/type/dialogTests/test_a");

        loadManager.build(new File("testing/files/loadmanager/individual/dialog_tests/test_a.yml"),
                testOut, new File("testing/files/loadmanager/individual/dialog_tests/"));

        LoadManager.getInstance().load(testOut);

        Map<String, DialogueRegister.Dialog> dialogMap = DialogueRegister.getInstance().getDialogs();

        assert dialogMap.get(LineUtils.lineFromMessage("[1/2] Caravan Driver: Agh!")) != null;
        assert dialogMap.get(LineUtils.lineFromMessage("[3/2] Caravan Driver: This dialogue is not real")) == null;
        assert dialogMap.get(LineUtils.lineFromMessage("[2/4] Soldier: There's a bit of trouble up ahead. You can't expect to get out of here alive if you aren't prepared."))
                .location.getLocation().equals(new VOWLocation(10, -10, -25));

        assert dialogMap.get(LineUtils.lineFromMessage("[1/4] Caravan Driver: I swear I hit this same dang boulder everytime I make this trip."))
                .location.getLocation().equals(new VOWLocation(10, 10, 10));

        EmptyFunctionProvider provider = new EmptyFunctionProvider() {
            @Override
            public VOWLocation getNpcLocationFromName(String name) {
                if (LineUtils.prepareName("Bob The Soldier").equals(name)) {
                    return new VOWLocation(-99, -99, -99);
                } else if (LineUtils.prepareName("Caravan Driver").equals(name)) {
                    return new VOWLocation(99, -99, -99);
                }
                return null;
            }
        };

        VOWCore.init(provider, new File(""));

        assert dialogMap.get(LineUtils.lineFromMessage("[3/3] Soldier: Hey, you three! Come over here."))
                .location.getLocation().equals(new VOWLocation(-99, -99, -99));

        assert dialogMap.get(LineUtils.lineFromMessage("[3/4] Caravan Driver: It's not that far, it's just a straight path from here."))
                .location.getLocation().equals(new VOWLocation(99, -99, -99));
    }
}
