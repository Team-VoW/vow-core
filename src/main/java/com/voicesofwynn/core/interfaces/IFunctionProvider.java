package com.voicesofwynn.core.interfaces;

import com.voicesofwynn.core.wrappers.VOWLocation;

import java.util.List;

public interface IFunctionProvider {
    VOWLocation getNpcLocationFromName(String name);

    VOWLocation getPlayerLocation();

}
