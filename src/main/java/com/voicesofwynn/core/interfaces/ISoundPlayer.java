package com.voicesofwynn.core.interfaces;


import com.voicesofwynn.core.wrappers.VOWLocation;

import java.io.InputStream;

public interface ISoundPlayer {

    /**
     * Plays sound at player
     * @param sound The sound which needs to be played
     */
    void playSound(InputStream sound);

    /**
     * Plays sound at location
     * @param sound The sound which needs to be played
     * @param location location
     */
    void playSound(InputStream sound, VOWLocation location);

    /**
     * Plays sound with a changing position using ISoundLocator
     * @param sound The sound which needs to be played
     * @param locator Sound Locator
     */
    void playSound(InputStream sound, ISoundLocator locator);



}
