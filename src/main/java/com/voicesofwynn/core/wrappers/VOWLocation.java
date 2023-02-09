package com.voicesofwynn.core.wrappers;

public class VOWLocation {
    public double x;
    public double y;
    public double z;

    public VOWLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double distanceTo(VOWLocation location) {
        return Math.sqrt(Math.pow(location.x - x, 2) + Math.pow(location.y - y, 2) + Math.pow(location.z - z, 2));
    }

    public double distanceToSquared(VOWLocation location) {
        return Math.pow(location.x - x, 2) + Math.pow(location.y - y, 2) + Math.pow(location.z - z, 2);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VOWLocation) {
            VOWLocation b = (VOWLocation) obj;
            return b.x == x && b.y == y && b.z == z;
        }
        return false;
    }

    public void add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void add(VOWLocation location) {
        this.x += location.x;
        this.y += location.y;
        this.z += location.z;
    }

}
