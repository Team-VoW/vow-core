package com.voicesofwynn.core.loadmanager;

import java.util.HashMap;
import java.util.Map;

public class LoadManager {

    private static LoadManager instance;
    public Map<String, RegisterType> register;

    public LoadManager () {
        register = new HashMap<>();
        instance = this;


    }

    public static LoadManager getInstance() {
        return instance;
    }
}
