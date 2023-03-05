package com.voicesofwynn.core.loadmanager.subtypes;

import com.voicesofwynn.core.VOWCore;
import com.voicesofwynn.core.utils.ByteUtils;
import com.voicesofwynn.core.utils.LineUtils;
import com.voicesofwynn.core.wrappers.VOWLocation;
import com.voicesofwynn.core.wrappers.VOWLocationProvider;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

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
                out.write(0);
                for (float f : coordinates) {
                    out.write(ByteUtils.encodeFloat(f));
                }
            }
        } else if (part instanceof Map) {
            Map<?, ?> parameters = ((Map<?, ?>) part);
            Set<String> used = new HashSet<>();

            if (parameters.get("npc") != null) {
                if (!(parameters.get("npc") instanceof String)) {
                    throw new RuntimeException("In " + context + "'s location npc is not a String, it's a " + parameters.get("npc").getClass().getName());
                }
                out.write(1);
                out.write(ByteUtils.encodeString(LineUtils.prepareName((String) parameters.get("npc"))));
                used.add("npc");
            } else {
                Object xO = parameters.get("x");
                Object yO = parameters.get("y");
                Object zO = parameters.get("z");

                float x, y, z;
                try {
                    x = Float.parseFloat(xO.toString());
                    y = Float.parseFloat(yO.toString());
                    z = Float.parseFloat(zO.toString());
                } catch (Exception e) {
                    throw new RuntimeException("In " + context + "'s location XYZ have a bad type");
                }

                out.write(0);
                out.write(ByteUtils.encodeFloat(x));
                out.write(ByteUtils.encodeFloat(y));
                out.write(ByteUtils.encodeFloat(z));
                used.add("x");
                used.add("y");
                used.add("z");
            }

            for (Object key : parameters.keySet()) {
                if (!used.contains((String)key)) {
                    throw new RuntimeException("In " + context + "'s location has the unused parameter " + key);
                }
            }
        } else {
            throw new RuntimeException("In " + context + " the coordinates wrong");

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
