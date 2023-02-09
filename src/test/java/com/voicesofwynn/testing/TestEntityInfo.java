package com.voicesofwynn.testing;

import com.voicesofwynn.core.wrappers.VOWLocation;

public class TestEntityInfo implements VOWEntityInfo {

    private String name;
    private VOWLocation location;
    private boolean hasArmour;
    private boolean isInvisible;

    public TestEntityInfo(String name, VOWLocation location, boolean hasArmour, boolean isInvisible) {
        this.name = name;
        this.location = location;
        this.hasArmour = hasArmour;
        this.isInvisible = isInvisible;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public VOWLocation getLocation() {
        return location;
    }

    @Override
    public boolean hasArmour() {
        return hasArmour;
    }

    @Override
    public boolean isInvisible() {
        return isInvisible;
    }
}
