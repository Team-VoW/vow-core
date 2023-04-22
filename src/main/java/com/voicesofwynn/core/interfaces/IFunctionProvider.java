package com.voicesofwynn.core.interfaces;

import com.voicesofwynn.core.wrappers.VOWLocation;
import com.voicesofwynn.core.wrappers.VOWLocationProvider;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This interface is used to play the sound files
 */

public interface IFunctionProvider {

    /**
     * This function must play a sound when called using VOWLocationProvider for its location.
     * @param file File of the sound that must be played
     */
    void playFileSound(File file, VOWLocationProvider location);
    VOWLocation getNpcLocationFromName(String name);

    VOWLocation getPlayerLocation();

    default Map<String, String[]> defaultSources () {
        return new HashMap<>();
    }

}
