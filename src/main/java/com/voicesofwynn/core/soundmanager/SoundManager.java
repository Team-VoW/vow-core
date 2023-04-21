package com.voicesofwynn.core.soundmanager;

public abstract class SoundManager {

    public static SoundManager instance;
    public static SoundManager getInstance() {
        return instance;
    }

    public abstract void start();

    public abstract void playSound(String name);

    /**
     * @return 0-1 float with progress
     */
    public abstract float getProgress();

}
