package com.voicesofwynn.core.soundmanager;

import com.voicesofwynn.core.wrappers.PlayEvent;

public abstract class SoundManager {

    public static SoundManager instance;
    public static SoundManager getInstance() {
        return instance;
    }

    public abstract void start();

    public abstract void playSound(String name, PlayEvent event);

    /**
     * @return 0-1 float with progress
     */
    public abstract float getProgress();

}
