package com.voicesofwynn.core.loadmanager.subtypes;

import com.voicesofwynn.core.VOWCore;
import com.voicesofwynn.core.utils.ByteUtils;
import com.voicesofwynn.core.wrappers.VOWLocation;
import com.voicesofwynn.core.wrappers.VOWLocationProvider;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationSubType {

    public static void writeLocation(FileOutputStream out, Object part, String context) throws IOException {
        if (part instanceof String) {
            String loc = (String)part;

            if (loc.equals("player")) {
                out.write(2);
            } else {
                String[] pos = loc.split("[ ,|/\\\\]"); // yes regex

                List<Float> coordinates = new ArrayList<>();
                for (String s : pos) {
                    try {
                        coordinates.add(Float.parseFloat(s));
                    } catch (Exception ignored) {}
                }
                if (coordinates.size() != 3) {
                    throw new RuntimeException("In " + context + " the list of cords [" + loc + "] has " + coordinates.size() + " coordinates instead of 3.");
                }
                for (float f : coordinates) {
                    out.write(ByteUtils.encodeFloat(f));
                }
            }

        }
    }

    /**
     * in case of name being null it will instead write player position based location
     */
    public static void writeNpcNameLocation(FileOutputStream out, String name) throws IOException {
        if (name == null) {
            out.write(2);
            return;
        }

        out.write(1);
        out.write(ByteUtils.encodeString(name));
    }

    public static VOWLocationProvider readLocation(FileInputStream in) throws IOException {

        // https://github.com/Team-VoW/vow-core/wiki/Types-in-code#location
        switch (ByteUtils.readByte(in)) {
            case 0:
                float x = ByteUtils.readFloat(in);
                float y = ByteUtils.readFloat(in);
                float z = ByteUtils.readFloat(in);
                return () -> new VOWLocation(x, y, z);
            case 1:
                String name = ByteUtils.readString(in);
                return () -> VOWCore.getFunctionProvider().getNpcLocationFromName(name);
            case 2:
                return () -> VOWCore.getFunctionProvider().getPlayerLocation();
        }

        return null;
    }

}
