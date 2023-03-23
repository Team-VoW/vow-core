package com.voicesofwynn.core.interfaces;

import com.voicesofwynn.core.wrappers.VOWLocation;

import java.util.HashMap;
import java.util.Map;

public interface IFunctionProvider {
    VOWLocation getNpcLocationFromName(String name);

    VOWLocation getPlayerLocation();

    default Map<String, String> defaultSources () {
        return new HashMap<>();
    }

}
