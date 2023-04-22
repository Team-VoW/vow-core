package com.voicesofwynn.testing;

import com.voicesofwynn.core.interfaces.IFunctionProvider;
import com.voicesofwynn.core.wrappers.VOWLocation;
import com.voicesofwynn.core.wrappers.VOWLocationProvider;

import java.io.File;

public class EmptyFunctionProvider implements IFunctionProvider {

    @Override
    public void playFileSound(File file, VOWLocationProvider location) {

    }

    @Override
    public VOWLocation getNpcLocationFromName(String name) {
        return null;
    }

    @Override
    public VOWLocation getPlayerLocation() {
        return null;
    }
}
