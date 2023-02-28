package com.voicesofwynn.core.loadmanager.types;

import com.voicesofwynn.core.loadmanager.LoadManager;

public class Types {

    public static void init(LoadManager m) {
        m.register(new DialogType());
        m.register(new FileBaseDirType());
    }

}
