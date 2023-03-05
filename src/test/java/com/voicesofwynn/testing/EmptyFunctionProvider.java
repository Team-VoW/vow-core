package com.voicesofwynn.testing;

import com.voicesofwynn.core.interfaces.IFunctionProvider;
import com.voicesofwynn.core.wrappers.VOWLocation;

public class EmptyFunctionProvider implements IFunctionProvider {
    @Override
    public VOWLocation getNpcLocationFromName(String name) {
        return null;
    }

    @Override
    public VOWLocation getPlayerLocation() {
        return null;
    }
}
