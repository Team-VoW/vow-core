package com.voicesofwynn.tests.wrappers;

import com.voicesofwynn.core.wrappers.VOWLocation;
import org.junit.jupiter.api.Test;

public class VOWLocationTest {

    @Test
    void equals() {
        VOWLocation a = new VOWLocation(0, 0, 0);
        a.x += 10;
        VOWLocation b = new VOWLocation(0, 0, 0);
        assert !a.equals(b);
        assert !a.equals(new Object());
        b = new VOWLocation(10, 0, 0);
        assert a.equals(b);

        a = new VOWLocation(10, 10, 10);
        b = new VOWLocation(10, 10, 10);
        assert a.equals(b);

        a = new VOWLocation(10, 10, 10);
        b = new VOWLocation(10, 10, 11);

        assert !a.equals(b);

        a = new VOWLocation(10, 10, 10);
        b = new VOWLocation(10, 11, 10);

        assert !a.equals(b);

        a = new VOWLocation(10, 10, 10);
        b = new VOWLocation(11, 10, 10);

        assert !a.equals(b);

        a = new VOWLocation(10, 11, 10);
        b = new VOWLocation(10, 11, 10);

        assert a.equals(b);
    }

    @Test
    void add() {
        VOWLocation a = new VOWLocation(0, 0, 0);
        a.x += 10;
        VOWLocation b = new VOWLocation(0, 0, 0);
        b.add(10, 0, 0);
        assert a.equals(b);

        b.add(a);

        assert b.x == 20;
    }

    @Test
    void distance() {
        VOWLocation a = new VOWLocation(0, 0, 0);
        VOWLocation b = new VOWLocation(0, 0, 0);

        assert a.distanceTo(b) == 0;
        assert b.distanceTo(a) == 0;
        assert a.distanceToSquared(b) == 0;
        assert b.distanceToSquared(a) == 0;

        a = new VOWLocation(0, 0, 0);
        b = new VOWLocation(10, 0, 0);

        assert a.distanceTo(b) == b.distanceTo(a);
        assert a.distanceTo(b) == 10;

        a = new VOWLocation(0, 0, 0);
        b = new VOWLocation(10, 10, 0);

        assert a.distanceTo(b) == b.distanceTo(a);
        assert Math.floor(a.distanceTo(b)) == 14;

        a = new VOWLocation(0, 0, 0);
        b = new VOWLocation(10, 10, 10);

        assert a.distanceTo(b) == b.distanceTo(a);
        assert Math.floor(a.distanceTo(b)) == 17;

        a = new VOWLocation(0, 0, 0);
        b = new VOWLocation(10, 10, 10);

        assert a.distanceTo(b) == Math.sqrt(a.distanceToSquared(b));

        a = new VOWLocation(0, 12, 0);
        b = new VOWLocation(10515, 10, 10);

        assert a.distanceTo(b) == Math.sqrt(a.distanceToSquared(b));
    }
}
