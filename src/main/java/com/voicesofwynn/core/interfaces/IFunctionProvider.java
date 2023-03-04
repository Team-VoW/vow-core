package com.voicesofwynn.core.interfaces;

import com.voicesofwynn.core.wrappers.VOWLocation;

public interface IFunctionProvider {
    VOWLocation getNpcLocationFromName(String name);

    VOWLocation getPlayerLocation();

}
